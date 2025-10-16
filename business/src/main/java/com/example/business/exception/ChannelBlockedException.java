package com.example.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ChannelBlockedException extends RuntimeException {
    public ChannelBlockedException(String message) {
        super(message);
    }
}
