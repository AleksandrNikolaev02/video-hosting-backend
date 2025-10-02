package com.example.business.controller;

import com.example.business.exception.UserNotFoundException;
import com.example.business.exception.VideoNotFoundException;
import dev.alex.auth.starter.auth_spring_boot_starter.exception.NoRightsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CustomControllerAdvice {
    @ExceptionHandler(value = NoRightsException.class)
    public ResponseEntity<String> handle(NoRightsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<String> handle(UserNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
                    log.error(error.getDefaultMessage());
                    errors.put(error.getField(), error.getDefaultMessage());
                }
        );
        return errors;
    }

    @ExceptionHandler(value = VideoNotFoundException.class)
    public ResponseEntity<String> handle(VideoNotFoundException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
