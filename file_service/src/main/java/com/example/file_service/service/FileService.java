package com.example.file_service.service;

import com.example.dto.Event;
import com.example.dto.FileDataDTO;
import com.example.dto.FileEventDTO;
import com.example.dto.FileResponseDTO;
import com.example.dto.Status;
import com.example.dto.VideoLoadDTO;
import com.example.file_service.config.FileConfig;
import com.example.file_service.dto.ChunkFileDTO;
import com.example.file_service.dto.SaveChunksDTO;
import com.example.file_service.exception.FileNotFoundByKeyException;
import com.example.file_service.exception.FileReadException;
import com.example.file_service.exception.FileStorageException;
import com.example.file_service.interfaces.Mapper;
import com.example.file_service.metric.CustomMetricService;
import com.example.file_service.model.FileEntity;
import com.example.file_service.model.PartFile;
import com.example.file_service.repository.FileEntityRepository;
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
    private final MinioClient minioClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final EventToTopicsStorage eventToTopicsStorage;
    private final CustomMetricService customMetricService;
    private final FileEntityRepository fileEntityRepository;
    private final Mapper mapper;

    @SneakyThrows
    @Transactional
    public void storeChunkFile(VideoLoadDTO dto, long userId, int index) {
        Optional<FileEntity> file = fileEntityRepository.findByKey(dto.key());

        if (!isBucketExists(fileConfig.getBucket())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(fileConfig.getBucket()).build());
        }

        if (file.isEmpty()) {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setUserId(userId);
            fileEntity.setFilename(UUID.randomUUID().toString());
            fileEntity.setContentType(dto.contentType());
            fileEntity.setKey(dto.key());
            fileEntity.setLength(0L);

            file = Optional.of(fileEntityRepository.save(fileEntity));
        }

        PartFile part = new PartFile();
        part.setFile(file.get());
        part.setPartName(dto.partName());
        part.setPartIndex(index);

        file.get().getParts().add(part);
        file.get().setLength(file.get().getLength() + dto.data().length);

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(fileConfig.getBucket())
                .object(userId + "/" + dto.partName())
                .stream(new ByteArrayInputStream(dto.data()), dto.data().length, -1)
                .contentType(dto.contentType())
                .build();

        saveFileInMinio(putObjectArgs);
    }

    @SneakyThrows
    @Transactional
    public void saveChunkFile(SaveChunksDTO dto) {
        List<ComposeSource> sources = new ArrayList<>();

        FileEntity file = fileEntityRepository.findByKey(dto.key()).orElseThrow(() -> {
            throw new FileNotFoundByKeyException(String.format("File not found by key: %d", dto.key()));
        });

        for (PartFile part : file.getParts()) {
            sources.add(ComposeSource.builder()
                    .bucket(fileConfig.getBucket())
                    .object(dto.userId() + "/" + part.getPartName())
                    .build());
        }

        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(fileConfig.getBucket())
                        .object(dto.userId() + "/" + file.getFilename())
                        .sources(sources)
                        .userMetadata(Map.of("Original-Content-Type", file.getContentType()))
                        .build()
        );
    }

    public Long findUniqueKeyForFile() {
        return fileEntityRepository.findMaxId() + 1;
    }

    public List<FileEntity> getFileEntitiesByUserId(Long userId) {
        return fileEntityRepository.findByUserId(userId);
    }

    @KafkaListener(topics = "${topics.file-events}",
                   groupId = "${spring.kafka.consumer.group-id}",
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

    @SneakyThrows
    private ObjectWriteResponse saveFileInMinio(PutObjectArgs args) {
        ObjectWriteResponse response = minioClient.putObject(args);

        log.info("Процесс: сохранение файла. Директория: {}", response.bucket() + "/" + response.object());

        return response;
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

        String path = userId + "/" + filename;

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
            contentType = fileEntityRepository.getContentTypeByFilename(filename);
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
                   groupId = "${spring.kafka.consumer.group-id}",
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
        return fileEntityRepository.getFileSize(filename);
    }
}
