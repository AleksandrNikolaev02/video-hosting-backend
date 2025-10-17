package com.example.camunda.service;

import com.example.camunda.config.CamundaConfig;
import com.example.camunda.dto.ExecutePipelineDTO;
import com.example.camunda.dto.PostMessageDTO;
import com.example.camunda.exceptions.ExecutePipelineException;
import com.example.camunda.util.IdGenerator;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CamundaService {
    private final RuntimeService camunda;
    private final CamundaConfig config;
    private final IdGenerator generator;

    public ExecutePipelineDTO startDeleteChannelPipeline() {
        try {
            String pipelineKey = String.format("%s_%s", config.getBusinessKey(),
                                                        generator.generate());

            camunda.startProcessInstanceByKey(config.getInstanceKey(),
                    pipelineKey);

            return new ExecutePipelineDTO(pipelineKey);
        } catch (Exception e) {
            throw new ExecutePipelineException("Error starting delete channel pipeline!");
        }
    }

    @KafkaListener(groupId = "${kafka.group-id}", topics = "${topics.publish-event-topic}")
    public void handleEvent(Object object) {
        PostMessageDTO dto = (PostMessageDTO) object;

        camunda.createMessageCorrelation(dto.status().name())
                .processInstanceBusinessKey(dto.pipelineKey())
                .correlate();
    }
}
