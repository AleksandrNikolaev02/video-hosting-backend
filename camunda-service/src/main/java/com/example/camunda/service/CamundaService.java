package com.example.camunda.service;

import com.example.camunda.config.CamundaConfig;
import com.example.camunda.config.ParametersConfig;
import com.example.camunda.dto.ExecutePipelineDTO;
import com.example.dto.PostMessageDTO;
import com.example.camunda.exceptions.ExecutePipelineException;
import com.example.camunda.util.IdGenerator;
import com.example.dto.StatusProcessChannel;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
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

            return new ExecutePipelineDTO(pipelineKey);
        } catch (Exception e) {
            throw new ExecutePipelineException("Error starting delete channel pipeline!");
        }
    }

    @KafkaListener(groupId = "${kafka.group-id}", topics = "${topics.publish-event-topic}")
    public void handleEvent(Object object) {
        PostMessageDTO dto = (PostMessageDTO) object;

        if (dto.status().equals(StatusProcessChannel.DATA_SUCCESS)) {
            Integer countSuccessRequest = (Integer) camunda.getVariable(dto.pipelineKey(),
                    parametersConfig.getCountSuccessRequestName());

            camunda.setVariable(dto.pipelineKey(),
                                parametersConfig.getCountSuccessRequestName(),
                              countSuccessRequest - 1);

            return;
        }

        camunda.createMessageCorrelation(dto.status().name())
                .processInstanceBusinessKey(dto.pipelineKey())
                .correlate();
    }
}
