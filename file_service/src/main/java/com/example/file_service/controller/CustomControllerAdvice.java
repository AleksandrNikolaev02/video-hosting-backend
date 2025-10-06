package com.example.file_service.controller;

import com.example.dto.FileDataDTO;
import com.example.dto.Status;
import com.example.file_service.exception.FileNotFoundByKeyException;
import com.example.file_service.exception.FileReadException;
import com.example.file_service.exception.FileNotFoundByUserIdException;
import com.example.file_service.exception.MinioException;
import com.example.file_service.exception.NoRightsException;
import com.example.file_service.exception.PreviewNotFoundByFilename;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
                    log.error(error.getDefaultMessage());
                    errors.put(error.getField(), error.getDefaultMessage());
                }
        );
        return errors;
    }

    @ExceptionHandler(value = PreviewNotFoundByFilename.class)
    public ResponseEntity<String> handle(PreviewNotFoundByFilename exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MinioException.class)
    public ResponseEntity<String> handle(MinioException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NoRightsException.class)
    public ResponseEntity<String> handle(NoRightsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }
}
