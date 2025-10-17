package com.example.camunda.delegate.channel;

import com.example.camunda.config.ParametersConfig;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class DeleteBusinessDataChannelDelegate implements JavaDelegate {
    private final ParametersConfig config;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Map<String, Object> variables = delegateExecution.getVariables();


    }
}
