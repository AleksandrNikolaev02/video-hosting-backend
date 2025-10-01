package com.example.file_service.exception;

public class MinioException extends RuntimeException {
    public MinioException(String message, Throwable exception) {
        super(message, exception);
    }
}
