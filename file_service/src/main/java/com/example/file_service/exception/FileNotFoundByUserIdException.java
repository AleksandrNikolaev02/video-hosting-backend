package com.example.file_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FileNotFoundByUserIdException extends RuntimeException {
    public FileNotFoundByUserIdException(String message) {
        super(message);
    }
}
