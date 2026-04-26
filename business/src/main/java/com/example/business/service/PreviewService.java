package com.example.business.service;

import com.example.business.config.TopicConfig;
import com.example.business.dto.CreateBasePreviewDTO;
import com.example.business.dto.CreateBasePreviewResponseDTO;
import com.example.business.dto.DeletePreviewDTO;
import com.example.business.model.Preview;
import com.example.business.model.Video;
import com.example.business.validator.DeleteStatusValidator;
import com.example.business.validator.PermissionValidator;
import com.example.dto.DeleteDataVideoEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class PreviewService {
    private final FindEntityService findEntityService;
    private final SaveEntityService saveEntityService;
    private final PermissionValidator validator;
    private final DeleteStatusValidator deleteStatusValidator;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicConfig topicConfig;
    private final ObjectMapper mapper = new ObjectMapper();

    public CreateBasePreviewResponseDTO createPreview(CreateBasePreviewDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.videoId());

        deleteStatusValidator.validate(video);
        validator.validateCreatorOfVideo(video, userId);

        Preview preview = new Preview();
        preview.setVideo(video);

        saveEntityService.save(preview);

        return new CreateBasePreviewResponseDTO(preview.getId());
    }

    @Transactional(rollbackFor = JsonProcessingException.class)
    public void deletePreview(DeletePreviewDTO dto, Long userId) throws JsonProcessingException {
        Video video = findEntityService.getVideoById(dto.videoId());

        deleteStatusValidator.validate(video);
        validator.validateCreatorOfVideo(video, userId);

        UUID previewId = video.getPreview().getId();

        video.setPreview(null);

        saveEntityService.save(video);

        DeleteDataVideoEvent event = new DeleteDataVideoEvent();
        event.setUserId(userId);
        event.setPreviewId(previewId);

        try {
            kafkaTemplate.send(topicConfig.getDeleteDataVideo(), 0,
                    "", mapper.writeValueAsString(event)).whenComplete(
                    (result, exc) -> {
                        if (exc == null) {
                            log.info("Сообщение успешно отправлено в топик {}", topicConfig.getDeleteDataVideo());
                        } else {
                            log.error("Сообщение отправлено с ошибкой", exc);
                        }
                    }
            );
        } catch (JsonProcessingException exception) {
            log.error("Ошибка сериализации ДТО!");
            throw exception;
        }
    }
}
