package com.example.file_service.service;

import com.example.dto.CreateVideoDTO;
import com.example.dto.Event;
import com.example.dto.FileDataDTO;
import com.example.dto.FileEventDTO;
import com.example.dto.FileResponseDTO;
import com.example.dto.Status;
import com.example.dto.VideoLoadDTO;
import com.example.file_service.config.FileConfig;
import com.example.file_service.config.TopicConfig;
import com.example.file_service.dto.ChunkFileDTO;
import com.example.file_service.dto.DeletePreviewDTO;
import com.example.file_service.dto.GetPreviewDTO;
import com.example.file_service.dto.RequestGetPreviewDTO;
import com.example.file_service.dto.SaveChunkResponseDTO;
import com.example.file_service.dto.SaveChunksDTO;
import com.example.file_service.dto.SavePreviewDTO;
import com.example.file_service.exception.FileNotFoundByKeyException;
import com.example.file_service.exception.FileReadException;
import com.example.file_service.exception.FileStorageException;
import com.example.file_service.exception.MinioException;
import com.example.file_service.exception.PreviewNotFoundByFilename;
import com.example.file_service.interfaces.Mapper;
import com.example.file_service.mapper.FileEntityMapper;
import com.example.file_service.metric.CustomMetricService;
import com.example.file_service.model.PreviewEntity;
import com.example.file_service.model.VideoEntity;
import com.example.file_service.model.PartFile;
import com.example.file_service.repository.PreviewEntityRepository;
import com.example.file_service.repository.VideoEntityRepository;
import com.example.file_service.util.EventToTopicsStorage;
import io.minio.BucketExistsArgs;
import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRange;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class FileService {
    private final FileConfig fileConfig;
    private final TopicConfig topicConfig;
    private final MinioClient minioClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, CreateVideoDTO> videoDTOKafkaTemplate;
    private final EventToTopicsStorage eventToTopicsStorage;
    private final CustomMetricService customMetricService;
    private final VideoEntityRepository videoEntityRepository;
    private final PreviewEntityRepository previewEntityRepository;
    private final Mapper mapper;
    private final FileEntityMapper fileEntityMapper;

    @SneakyThrows
    @Transactional
    public SaveChunkResponseDTO storeChunkFile(VideoLoadDTO dto, long userId,
                                               int index, String originalFilename) {
        Optional<VideoEntity> file = videoEntityRepository.findByKey(dto.key());

        SaveChunkResponseDTO saveChunkResponseDTO = new SaveChunkResponseDTO();

        if (!isBucketExists(fileConfig.getBucket())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(fileConfig.getBucket()).build());
        }

        if (file.isEmpty()) {
            VideoEntity videoEntity = new VideoEntity();
            String filename = UUID.randomUUID().toString();
            saveChunkResponseDTO.setFilename(filename);

            videoEntity.setUserId(userId);
            videoEntity.setFilename(filename);
            videoEntity.setContentType(dto.contentType());
            videoEntity.setKey(dto.key());
            videoEntity.setLength(0L);
            videoEntity.setOriginalFilename(originalFilename);

            file = Optional.of(videoEntityRepository.save(videoEntity));
        }

        PartFile part = new PartFile();
        part.setFile(file.get());
        part.setPartName(dto.partName());
        part.setPartIndex(index);

        file.get().getParts().add(part);
        file.get().setLength(file.get().getLength() + dto.data().length);

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(fileConfig.getBucket())
                .object(String.format("%d/%s/%s/%s", userId,fileConfig.getVideoPath(),
                        file.get().getFilename(), dto.partName()))
                .stream(new ByteArrayInputStream(dto.data()), dto.data().length, -1)
                .contentType(dto.contentType())
                .build();

        saveFileInMinio(putObjectArgs);

        return saveChunkResponseDTO;
    }

    @SneakyThrows
    @Transactional
    public void saveChunkFile(SaveChunksDTO dto) {
        List<ComposeSource> sources = new ArrayList<>();

        VideoEntity file = videoEntityRepository.findByKey(dto.key()).orElseThrow(() ->
            new FileNotFoundByKeyException(String.format("File not found by key: %s", dto.key()))
        );

        for (PartFile part : file.getParts()) {
            sources.add(ComposeSource.builder()
                    .bucket(fileConfig.getBucket())
                    .object(String.format("%d/%s/%s/%s", dto.userId(),fileConfig.getVideoPath(),
                            file.getFilename(), part.getPartName()))
                    .build());
        }

        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(fileConfig.getBucket())
                        .object(String.format("%d/%s/%s", dto.userId(),fileConfig.getVideoPath(),
                                file.getFilename()))
                        .sources(sources)
                        .userMetadata(Map.of("Original-Content-Type", file.getContentType()))
                        .build()
        );

        videoDTOKafkaTemplate.send(topicConfig.getCreateVideo(),
                                   fileEntityMapper.getCreateVideoDtoFromVideoEntity(file));
    }

    public String findUniqueKeyForFile() {
        return UUID.randomUUID().toString();
    }

    public List<VideoEntity> getFileEntitiesByUserId(Long userId) {
        return videoEntityRepository.findByUserId(userId);
    }

    @KafkaListener(topics = "${topics.file-events}",
                   groupId = "${kafka.group-id}",
                   errorHandler = "customKafkaErrorHandler")
    @SneakyThrows
    public void storeFile(String event) {
        FileEventDTO fileEventDTO = mapper.deserialize(event, FileEventDTO.class);

        log.info("Процесс: создание директории. Директория: {}", fileConfig.getBucket());

        if (!isBucketExists(fileConfig.getBucket())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(fileConfig.getBucket()).build());
        }

        PutObjectArgs args = createPutObjectArgs(fileEventDTO);
        ObjectWriteResponse response = saveFileInMinio(args);

        FileResponseDTO fileResponseDTO = createFileResponseDTO(fileEventDTO, response);

        String topic = definingTopicRelativeEvent(fileEventDTO.getEvent());

        customMetricService.incrementSuccessMinioServiceMetric();

        kafkaTemplate.send(topic, mapper.serialize(fileResponseDTO));
    }

    public void savePreview(SavePreviewDTO dto, Long userId) {
        String filename = UUID.randomUUID().toString();

        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(fileConfig.getBucket())
                .object(String.format("%d/%s/%s", userId, fileConfig.getPreviewPath(), filename))
                .stream(new ByteArrayInputStream(dto.data()), dto.data().length, -1)
                .contentType(dto.contentType())
                .build();

        saveFileInMinio(args);

        previewEntityRepository.save(createPreviewEntity(filename, dto, userId));
    }

    public GetPreviewDTO getPreview(RequestGetPreviewDTO dto) {
        PreviewEntity preview = previewEntityRepository.findByFilename(dto.filename()).orElseThrow(
                () -> new PreviewNotFoundByFilename(String.format("File not found by filename: %s", dto.filename()))
        );

        String path = String.format("%d/%s/%s", preview.getUserId(), fileConfig.getPreviewPath(), dto.filename());
        FileDataDTO fileDataDTO = getFile(path);

        return new GetPreviewDTO(fileDataDTO.getData(), preview.getContentType());
    }

    @SneakyThrows
    public void deletePreview(DeletePreviewDTO dto) {
        PreviewEntity preview = previewEntityRepository.findByFilename(dto.filename()).orElseThrow(
                () -> new PreviewNotFoundByFilename(String.format("File not found by filename: %s", dto.filename()))
        );

        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(fileConfig.getBucket())
                .object(String.format("%d/%s/%s", preview.getUserId(), fileConfig.getPreviewPath(), dto.filename()))
                .build();

        minioClient.removeObject(args);
    }

    private PreviewEntity createPreviewEntity(String filename, SavePreviewDTO dto, Long userId) {
        PreviewEntity preview = new PreviewEntity();

        preview.setLength((long) dto.data().length);
        preview.setUserId(userId);
        preview.setContentType(dto.contentType());
        preview.setFilename(filename);

        return preview;
    }

    @SneakyThrows
    private boolean isBucketExists(String bucket) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    }

    private PutObjectArgs createPutObjectArgs(FileEventDTO dto) {
        return PutObjectArgs.builder()
                .bucket(fileConfig.getBucket())
                .object(dto.getDir() + "/" + dto.getFilename())
                .stream(new ByteArrayInputStream(dto.getFileData()), dto.getFileData().length, -1)
                .contentType(dto.getContentType())
                .build();
    }

    private ObjectWriteResponse saveFileInMinio(PutObjectArgs args) {
        try {
            ObjectWriteResponse response = minioClient.putObject(args);
            log.info("Процесс: сохранение файла. Директория: {}", response.bucket() + "/" + response.object());
            return response;
        } catch (Exception exception) {
            throw new MinioException("Error saving file in Minio!", exception);
        }
    }

    private FileResponseDTO createFileResponseDTO(FileEventDTO dto, ObjectWriteResponse response) {
        return new FileResponseDTO(
                dto.getFilename(),
                response.object(),
                dto.getArtifactId()
        );
    }

    private String definingTopicRelativeEvent(Event event) {
        return eventToTopicsStorage.getTopicByEvent(event);
    }

    public FileDataDTO getFile(String path) {
        byte[] fileData;
        try {
            fileData = getFileFromMinio(fileConfig.getBucket(), path);
        } catch (Exception e) {
            customMetricService.incrementErrorMinioServiceMetric();
            throw new FileReadException("Error reading file!");
        }

        customMetricService.incrementSuccessMinioServiceMetric();

        return createFileDataDTO(fileData);
    }

    @SneakyThrows
    public ChunkFileDTO getChunkFile(String filename, long userId, String rangeHeader) {
        final long chunkSize = fileConfig.getChunkSize();
        final long fileLength = getFileSize(filename);

        long start = 0;
        long end = Math.min(chunkSize - 1, fileLength - 1);

        if (rangeHeader != null) {
            HttpRange range = HttpRange.parseRanges(rangeHeader).get(0);
            start = range.getRangeStart(fileLength);

            end = Math.min(start + chunkSize - 1, fileLength - 1);
        }

        String path = String.format("%d/%s/%s", userId, fileConfig.getVideoPath(), filename);

        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(fileConfig.getBucket())
                .object(path)
                .offset(start)
                .length(end - start + 1)
                .build();

        var response = minioClient.getObject(args);

        if (response == null) {
            throw new FileStorageException(String.format("Файл не найден в MinIO по пути: %s", path));
        }

        log.info("Процесс: получение файла из Minio. Часть файла {} размером {} байт успешно получена из Minio!",
                path, end - start + 1);

        return new ChunkFileDTO(response.readAllBytes(),
                                getContentTypeByFilename(filename), start, end, fileLength);
    }

    private String getContentTypeByFilename(String filename) {
        String contentType = redisTemplate.opsForValue().get(filename);

        if (contentType == null) {
            contentType = videoEntityRepository.getContentTypeByFilename(filename);
            redisTemplate.opsForValue().set(filename, contentType);
        }

        return contentType;
    }

    private byte[] getFileFromMinio(String bucket, String path) throws Exception {
        GetObjectArgs args = createGetObjectsArgsByBucketAndPath(
                bucket,
                path);
        InputStream inputStream = minioClient.getObject(args);

        if (inputStream == null) {
            throw new FileStorageException(String.format("Файл не найден в MinIO по пути: %s", path));
        }

        log.info("Процесс: получение файла из Minio. Файл успешно получен из MinIO: {}", path);

        return inputStream.readAllBytes();
    }

    private GetObjectArgs createGetObjectsArgsByBucketAndPath(String bucket, String path) {
        return GetObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build();
    }

    private FileDataDTO createFileDataDTO(byte[] fileData) {
        return new FileDataDTO(fileData, Status.OK);
    }

    @KafkaListener(topics = "${topics.delete-file-request}",
                   groupId = "${kafka.group-id}",
                   errorHandler = "customKafkaErrorHandler")
    @SneakyThrows
    public void deleteDirectory(String path) {
        log.info("Процесс удаления дериктории {}: начало процесса удаления директории.", path);

        List<DeleteObject> objectsToDelete = createListObjectsToDelete(fileConfig.getBucket(), path);

        removeAllObjectsToDelete(fileConfig.getBucket(), objectsToDelete);

        customMetricService.incrementSuccessMinioServiceMetric();

        log.info("Процесс удаления дериктории {}: удаление директории завершено.", path);
    }

    private List<DeleteObject> createListObjectsToDelete(String bucket, String path) throws Exception {
        Iterable<Result<Item>> items = minioClient.listObjects(
                createListObjectsArgs(bucket, path)
        );

        List<DeleteObject> objectsToDelete = new ArrayList<>();
        for (Result<Item> item : items) {
            objectsToDelete.add(new DeleteObject(item.get().objectName()));
        }

        return objectsToDelete;
    }

    private ListObjectsArgs createListObjectsArgs(String bucket, String path) {
        return ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(path)
                .recursive(true)
                .build();
    }

    private void removeAllObjectsToDelete(String bucket, List<DeleteObject> objectsToDelete) {
        minioClient.removeObjects(
                createRemoveObjectsArgs(bucket, objectsToDelete)
        );
    }

    private RemoveObjectsArgs createRemoveObjectsArgs(String bucket, List<DeleteObject> objectsToDelete) {
        return RemoveObjectsArgs.builder()
                .bucket(bucket)
                .objects(objectsToDelete)
                .build();
    }

    private long getFileSize(String filename) {
        return videoEntityRepository.getFileSize(filename);
    }
}
