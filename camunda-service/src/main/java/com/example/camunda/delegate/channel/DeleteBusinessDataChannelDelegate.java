package com.example.camunda.delegate.channel;

import com.example.camunda.client.BusinessService;
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
@Component("deleteBusinessDataChannelDelegate")
@AllArgsConstructor
public class DeleteBusinessDataChannelDelegate implements JavaDelegate {
    private final ParametersConfig config;
    private final BusinessService businessService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicConfig topicConfig;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Начало выполнения делегата для удаления бизнес данных канала...");

        Map<String, Object> variables = delegateExecution.getVariables();

        Long userId = (Long) variables.get(config.getUserId());
        String pipelineKey = (String) variables.get(config.getNamePipelineKey());

        ResponseEntity<Void> response = businessService.deleteBusinessDataChannel(userId, pipelineKey);

        if (response.getStatusCode().isError()) {
            log.error("Запрос на бизнес-сервис вернул статус 4xx или 5xx!");

            PostMessageDTO dto = new PostMessageDTO(StatusProcessChannel.DATA_FAILURE,
                                                    pipelineKey);

            kafkaTemplate.send(topicConfig.getPublishEventTopic(), dto);
        }
    }
}
