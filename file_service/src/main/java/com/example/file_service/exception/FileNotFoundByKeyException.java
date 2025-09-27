package com.example.file_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class FileNotFoundByKeyException extends RuntimeException {
    public FileNotFoundByKeyException(String message) {
        super(message);
    }
}
