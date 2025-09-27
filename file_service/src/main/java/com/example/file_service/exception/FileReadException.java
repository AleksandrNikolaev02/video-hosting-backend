package com.example.file_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FileReadException extends RuntimeException {
    public FileReadException(String message) {
        super(message);
    }
}
