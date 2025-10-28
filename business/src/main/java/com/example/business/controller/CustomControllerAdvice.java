package com.example.business.controller;

import com.example.business.exception.ChannelAlreadyDeletedException;
import com.example.business.exception.ChannelAlreadyExistsException;
import com.example.business.exception.ChannelBlockedException;
import com.example.business.exception.ChannelNotFoundException;
import com.example.business.exception.CommentNotFoundException;
import com.example.business.exception.PlaylistAlreadyDeletedException;
import com.example.business.exception.PlaylistNotFoundException;
import com.example.business.exception.RequestNotFoundException;
import com.example.business.exception.SubscribeAlreadyExistException;
import com.example.business.exception.SubscriptionNotFoundException;
import com.example.business.exception.UserNotCreateChannelException;
import com.example.business.exception.UserNotFoundException;
import com.example.business.exception.VideoAlreadyDeletedException;
import com.example.business.exception.VideoAlreadyEvaluateException;
import com.example.business.exception.VideoNotFoundException;
import dev.alex.auth.starter.auth_spring_boot_starter.exception.NoRightsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(value = VideoAlreadyEvaluateException.class)
    public ResponseEntity<String> handle(VideoAlreadyEvaluateException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handle(DataIntegrityViolationException exception) {
        Map<String, String> description = new HashMap<>();
        description.put("message", "Запись с таким значением уже есть в таблице!");

        log.error(exception.getMessage());
        return ResponseEntity.status(409).body(description);
    }

    @ExceptionHandler(value = PlaylistNotFoundException.class)
    public ResponseEntity<String> handle(PlaylistNotFoundException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ChannelAlreadyExistsException.class)
    public ResponseEntity<String> handle(ChannelAlreadyExistsException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UserNotCreateChannelException.class)
    public ResponseEntity<String> handle(UserNotCreateChannelException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ChannelNotFoundException.class)
    public ResponseEntity<String> handle(ChannelNotFoundException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RequestNotFoundException.class)
    public ResponseEntity<String> handle(RequestNotFoundException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ChannelBlockedException.class)
    public ResponseEntity<String> handle(ChannelBlockedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ChannelAlreadyDeletedException.class)
    public ResponseEntity<String> handle(ChannelAlreadyDeletedException exception) {
        log.error(exception.getMessage());

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = PlaylistAlreadyDeletedException.class)
    public ResponseEntity<String> handle(PlaylistAlreadyDeletedException exception) {
        log.error(exception.getMessage());

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = VideoAlreadyDeletedException.class)
    public ResponseEntity<String> handle(VideoAlreadyDeletedException exception) {
        log.error(exception.getMessage());

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CommentNotFoundException.class)
    public ResponseEntity<String> handle(CommentNotFoundException exception) {
        log.error(exception.getMessage());

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = SubscribeAlreadyExistException.class)
    public ResponseEntity<String> handle(SubscribeAlreadyExistException exception) {
        log.error(exception.getMessage());

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = SubscriptionNotFoundException.class)
    public ResponseEntity<String> handle(SubscriptionNotFoundException exception) {
        log.error(exception.getMessage());

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
