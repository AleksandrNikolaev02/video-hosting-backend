package com.example.camunda.delegate.channel;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("successDeleteDataChannelDelegate")
public class SuccessDeleteDataChannelDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Данные с бизнес-микросервиса успешно удалены!");
    }
}
