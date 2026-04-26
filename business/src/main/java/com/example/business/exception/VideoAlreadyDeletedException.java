package com.example.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class VideoAlreadyDeletedException extends RuntimeException {
    public VideoAlreadyDeletedException(String message) {
        super(message);
    }
}
