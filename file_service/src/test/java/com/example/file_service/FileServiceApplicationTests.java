package com.example.file_service;

import com.example.dto.FileDataDTO;
import com.example.dto.FileEventDTO;
import com.example.dto.FileResponseDTO;
import com.example.dto.Status;
import com.example.dto.Event;
import com.example.dto.VideoLoadDTO;
import com.example.file_service.extensions.KafkaExtension;
import com.example.file_service.extensions.MinioExtension;
import com.example.file_service.extensions.PostgresExtension;
import com.example.file_service.mapper.JsonMapper;
import com.example.file_service.model.FileEntity;
import com.example.file_service.repository.FileEntityRepository;
import com.example.file_service.service.FileService;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({KafkaExtension.class, MinioExtension.class, PostgresExtension.class})
public class FileServiceApplicationTests {
    private static final String FILE_EVENTS_TOPIC = "file-events";
    private static final String FILE_RESPONSES_TOPIC = "file-responses";
    private static final String GET_FILE_REQUEST_TOPIC = "get-file-request";
    private static final String GET_FILE_REPLY_TOPIC = "get-file-reply";
    private static final Integer TEST_ARTIFACT_ID = 1;
    private static final String TEST_FILENAME = "test.txt";
    private static final String TEST_DIR_NAME = String.format("%d/test", TEST_ARTIFACT_ID);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private JsonMapper jsonMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Test
    public void testStoreChunkFile() throws IOException {
        Path path = Path.of("src/test/resources/test_file.mp4");
        byte[] file = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);

        int length = file.length;
        final int chunkSize = 5_242_880;
        int count = length / chunkSize + (length % chunkSize == 0 ? 0 : 1);

        for (int i = 0; i < count; ++i) {
            VideoLoadDTO dto = new VideoLoadDTO(Arrays.copyOfRange(file, 0, chunkSize),
                    contentType, "part" + i);
            fileService.storeChunkFile(dto, 1, i);
        }

        fileService.saveChunkFile(1);

        Optional<FileEntity> fileEntity = fileEntityRepository.findByUserId(1L);
        assertDoesNotThrow(() -> fileService.getFile(String.format("1/%s", fileEntity.get().getFilename())));
        var dto = fileService.getFile(String.format("1/%s", fileEntity.get().getFilename()));

        assertEquals(dto.getStatus(), Status.OK);
    }

//    @Test
//    public void testStoreFile() throws IOException {
//        FileEventDTO dto = createFileEventDto();
//        kafkaTemplate.send(FILE_EVENTS_TOPIC, jsonMapper.serialize(dto));
//
//        KafkaConsumer<String, String> consumer = getStringStringKafkaConsumer();
//        consumer.subscribe(Collections.singletonList(FILE_RESPONSES_TOPIC));
//
//        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
//
//        assertFalse(records.isEmpty(), "Empty topic!");
//
//        records.forEach(record -> {
//            var resultDto = jsonMapper.deserialize(record.value(), FileResponseDTO.class);
//            assertEquals(1, resultDto.getArtifactId());
//            assertEquals(TEST_FILENAME, resultDto.getFilename());
//        });
//
//        consumer.close();
//    }
//
//    @Test
//    public void testGetFile() throws IOException {
//        String path = String.format("%s/%s", TEST_DIR_NAME, TEST_FILENAME);
//
//        FileEventDTO dto = createFileEventDto();
//        kafkaTemplate.send(FILE_EVENTS_TOPIC, jsonMapper.serialize(dto));
//
//        await()
//            .atMost(15, TimeUnit.SECONDS)
//            .pollInterval(500, TimeUnit.MILLISECONDS)
//            .until(() -> {
//                try {
//                    minioClient.statObject(StatObjectArgs.builder()
//                            .bucket(System.getProperty("file.dir"))
//                            .object(path)
//                            .build());
//                    return true;
//                } catch (Exception e) {
//                    return false;
//                }
//            });
//
//        kafkaTemplate.send(GET_FILE_REQUEST_TOPIC, path);
//
//        KafkaConsumer<String, String> consumer = getStringStringKafkaConsumer();
//        consumer.subscribe(Collections.singletonList(GET_FILE_REPLY_TOPIC));
//
//        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
//
//        assertFalse(records.isEmpty(), "Empty topic!");
//
//        records.forEach(record -> {
//            var resultDto = jsonMapper.deserialize(record.value(), FileDataDTO.class);
//            assertEquals(Status.OK, resultDto.getStatus());
//        });
//
//        consumer.close();
//    }

    private static @NotNull KafkaConsumer<String, String> getStringStringKafkaConsumer() {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("spring.kafka.bootstrap-servers"));
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, System.getProperty("spring.kafka.consumer.group-id"));
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        return new KafkaConsumer<>(consumerProps);
    }

    private FileEventDTO createFileEventDto() throws IOException {
        FileEventDTO dto = new FileEventDTO();
        MultipartFile file = createEmptyMultipartFile();

        dto.setDir(TEST_DIR_NAME);
        dto.setFilename(file.getOriginalFilename());
        dto.setEvent(Event.ARTIFACT);
        dto.setContentType(file.getContentType());
        dto.setFileData(file.getBytes());
        dto.setArtifactId(TEST_ARTIFACT_ID);

        return dto;
    }

    private MultipartFile createEmptyMultipartFile() {
        return new MockMultipartFile(
                "file",
                TEST_FILENAME,
                "text/plain",
                new byte[0]
        );
    }
}
