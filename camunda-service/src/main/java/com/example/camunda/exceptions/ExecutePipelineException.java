package com.example.camunda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ExecutePipelineException extends RuntimeException {
    public ExecutePipelineException(String message) {
        super(message);
    }
}
