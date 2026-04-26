package com.example.camunda.delegate.channel;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("successDeleteChannelDelegate")
public class SuccessDeleteChannelDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Пайплайн для удаления канала выполнился успешно!");
    }
}
