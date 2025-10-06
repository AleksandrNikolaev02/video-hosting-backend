package com.example.file_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class NoRightsException extends RuntimeException {
    public NoRightsException(String message) {
        super(message);
    }
}
