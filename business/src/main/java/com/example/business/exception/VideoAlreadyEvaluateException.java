package com.example.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class VideoAlreadyEvaluateException extends RuntimeException {
    public VideoAlreadyEvaluateException(String message) {
        super(message);
    }
}
