package com.example.camunda.delegate.channel;

import com.example.camunda.config.ParametersConfig;
import com.example.camunda.config.TopicConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@Component("failureDeleteDataChannelDelegate")
public class FailureDeleteDataChannelDelegate implements JavaDelegate {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicConfig topicConfig;
    private final ParametersConfig config;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Запуск компенсирующих транзакций...");

        Map<String, Object> params = delegateExecution.getVariables();

        kafkaTemplate.send(topicConfig.getCompensatingTransactionBusinessService(),
                   0, "", params.get(config.getUserId()));

        kafkaTemplate.send(topicConfig.getCompensatingTransactionBusinessService(),
                   1, "", params.get(config.getUserId()));
    }
}
