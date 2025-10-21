package com.example.camunda.delegate.channel;

import com.example.camunda.client.FileService;
import com.example.camunda.config.ParametersConfig;
import com.example.camunda.config.TopicConfig;
import com.example.dto.PostMessageDTO;
import com.example.dto.StatusProcessChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@Component("deleteFileDataChannelDelegate")
public class DeleteFileDataChannelDelegate implements JavaDelegate {
    private final FileService fileService;
    private final ParametersConfig parametersConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicConfig topicConfig;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Начала выполнения делегата для удаления файлов канала...");

        Map<String, Object> variables = delegateExecution.getVariables();

        ResponseEntity<Void> response = fileService.deleteChannel((Long) variables.get(
                parametersConfig.getUserId()),
                (String) variables.get(
                        parametersConfig.getNamePipelineKey()
                )
        );

        if (response.getStatusCode().isError()) {
            log.error("Запрос к микросервису вернул статус 4xx или 5xx");
            String pipelineKey = (String) variables.get(parametersConfig.getNamePipelineKey());
            PostMessageDTO dto = new PostMessageDTO(StatusProcessChannel.FILE_FAILURE, pipelineKey);

            kafkaTemplate.send(topicConfig.getPublishEventTopic(), dto);
        }
    }
}
