package com.example.auth_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class KafkaSendMessageException extends RuntimeException {
    public KafkaSendMessageException(String message) {
        super(message);
    }
}
