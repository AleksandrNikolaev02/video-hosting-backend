package com.example.camunda.service;

import com.example.camunda.config.CamundaConfig;
import com.example.camunda.config.ParametersConfig;
import com.example.camunda.dto.ExecutePipelineDTO;
import com.example.dto.PostMessageDTO;
import com.example.camunda.exceptions.ExecutePipelineException;
import com.example.camunda.util.IdGenerator;
import com.example.dto.StatusProcessChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class CamundaService {
    private final RuntimeService camunda;
    private final CamundaConfig config;
    private final IdGenerator generator;
    private final ParametersConfig parametersConfig;

    public ExecutePipelineDTO startDeleteChannelPipeline(Map<String, Object> params) {
        try {
            String pipelineKey = String.format("%s_%s", config.getBusinessKey(),
                                                        generator.generate());

            params.put(parametersConfig.getNamePipelineKey(), pipelineKey);

            camunda.startProcessInstanceByKey(config.getInstanceKey(),
                                              pipelineKey, params);

            log.info("Запуск пайплайна завершился успешно!");

            return new ExecutePipelineDTO(pipelineKey);
        } catch (Exception e) {
            log.error("При запуске пайплайна произошла ошибка: {}", e.getMessage());

            throw new ExecutePipelineException("Error starting delete channel pipeline!");
        }
    }

    @KafkaListener(groupId = "${kafka.group-id}",
                   topics = "${topics.publish-event-topic}",
                   containerFactory = "postMessageDTOContainerFactory")
    public void handleEvent(PostMessageDTO dto) {
        log.info("Начало обработки события Kafka...");

        if (dto.status().equals(StatusProcessChannel.DATA_SUCCESS)) {
            log.info("Получен один их успешных ответов на удаление канала с микросервиса бизнес данных!");

            ProcessInstance process = camunda.createProcessInstanceQuery()
                    .processInstanceBusinessKey(dto.pipelineKey())
                    .singleResult();

            if (process != null) {
                log.info("Процесс с business-key: {} найден", dto.pipelineKey());

                Integer countSuccessRequest = (Integer) camunda.getVariable(process.getId(),
                        parametersConfig.getCountSuccessRequestName());

                camunda.setVariable(process.getId(),
                        parametersConfig.getCountSuccessRequestName(),
                        countSuccessRequest - 1);

                if (countSuccessRequest - 1 == 0) {
                    camunda.createMessageCorrelation(StatusProcessChannel.DATA_SUCCESS.name())
                            .processInstanceBusinessKey(dto.pipelineKey())
                            .setVariable(parametersConfig.getCountSuccessRequestName(), 0)
                            .correlate();
                }

                log.info("Окончание обработки события Kafka...");
            } else {
                log.error("Процесса с именем: {} не сущесвует!", dto.pipelineKey());
            }

            return;
        }

        camunda.createMessageCorrelation(dto.status().name())
                .processInstanceBusinessKey(dto.pipelineKey())
                .correlate();

        log.info("Окончание обработки события Kafka...");
    }
}
