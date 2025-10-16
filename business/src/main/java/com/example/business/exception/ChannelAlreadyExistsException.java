package com.example.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ChannelAlreadyExistsException extends RuntimeException {
    public ChannelAlreadyExistsException(String message) {
        super(message);
    }
}
