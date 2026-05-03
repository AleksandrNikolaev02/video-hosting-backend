package com.example.auth_service.contrtoller;

import com.example.auth_service.exceptions.AccessDeniedException;
import com.example.auth_service.exceptions.KafkaSendMessageException;
import com.example.auth_service.exceptions.MicroserviceUnavailableException;
import com.example.auth_service.exceptions.RefreshTokenNotFoundException;
import com.example.auth_service.exceptions.RestSendMessageClientException;
import com.example.auth_service.exceptions.RoleNotFoundException;
import com.example.auth_service.exceptions.TokenRefreshException;
import com.example.auth_service.exceptions.TwoFactorAuthenticationException;
import com.example.auth_service.exceptions.UserAlreadyExistsException;
import com.example.auth_service.exceptions.UserNotFoundException;
import com.example.auth_service.exceptions.UserSettingNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomControllerAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> exceptionHandler(UserNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> exceptionHandler(RoleNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(KafkaSendMessageException.class)
    public ResponseEntity<String> exceptionHandler(KafkaSendMessageException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<String> exceptionHandler(RefreshTokenNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<String> exceptionHandler(TokenRefreshException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserSettingNotFoundException.class)
    public ResponseEntity<String> exceptionHandler(UserSettingNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> exceptionHandler(AccessDeniedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MicroserviceUnavailableException.class)
    public ResponseEntity<String> exceptionHandler(MicroserviceUnavailableException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(TwoFactorAuthenticationException.class)
    public ResponseEntity<String> exceptionHandler(TwoFactorAuthenticationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RestSendMessageClientException.class)
    public ResponseEntity<String> exceptionHandler(RestSendMessageClientException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<String> exceptionHandler(DisabledException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> exceptionHandler(UserAlreadyExistsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
