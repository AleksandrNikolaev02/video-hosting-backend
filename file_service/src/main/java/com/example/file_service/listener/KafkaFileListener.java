package com.example.file_service.listener;

import com.example.dto.FileDataDTO;
import com.example.file_service.mapper.JsonMapper;
import com.example.file_service.service.FileService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaFileListener {
    private final FileService fileService;
    private final JsonMapper mapper;

    @KafkaListener(topics = "${topics.get-file-request}",
            groupId = "${spring.kafka.consumer.group-id}",
            errorHandler = "customKafkaErrorHandler")
    @SendTo("#{topicConfig.getGetFileReply()}")
    @SneakyThrows
    public String getFile(String path) {
        FileDataDTO dto = fileService.getFile(path);

        return mapper.serialize(dto);
    }
}
