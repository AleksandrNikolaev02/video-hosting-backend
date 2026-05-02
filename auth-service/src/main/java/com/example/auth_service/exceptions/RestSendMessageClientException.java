package com.example.auth_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RestSendMessageClientException extends RuntimeException {
    public RestSendMessageClientException(String message) {
        super(message);
    }
}
