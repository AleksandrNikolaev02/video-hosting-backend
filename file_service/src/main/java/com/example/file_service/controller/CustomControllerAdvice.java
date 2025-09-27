package com.example.file_service.controller;

import com.example.dto.FileDataDTO;
import com.example.dto.Status;
import com.example.file_service.exception.FileNotFoundByKeyException;
import com.example.file_service.exception.FileReadException;
import com.example.file_service.exception.FileNotFoundByUserIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomControllerAdvice {
    @ExceptionHandler(value = FileReadException.class)
    public ResponseEntity<FileDataDTO> handle(FileReadException exception) {
        return new ResponseEntity<>(new FileDataDTO(null, Status.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = FileNotFoundByUserIdException.class)
    public ResponseEntity<String> handle(FileNotFoundByUserIdException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = FileNotFoundByKeyException.class)
    public ResponseEntity<String> handle(FileNotFoundByKeyException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
