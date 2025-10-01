package com.example.business.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component(value = "createVideoHandler")
@Slf4j
public class KafkaCreateVideoHandler implements KafkaListenerErrorHandler {

    @Override
    @Nullable
    public Object handleError(@NonNull Message<?> message, @NonNull ListenerExecutionFailedException exception) {
        Throwable cause = exception.getCause();

        log.error("Exception {} with error: {}", cause, exception.getMessage());

        // FIXME
        return null;
    }
}
