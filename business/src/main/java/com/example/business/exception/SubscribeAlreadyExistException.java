package com.example.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SubscribeAlreadyExistException extends RuntimeException {
    public SubscribeAlreadyExistException(String message) {
        super(message);
    }
}
