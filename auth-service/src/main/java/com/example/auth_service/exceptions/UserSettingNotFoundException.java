package com.example.auth_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserSettingNotFoundException extends RuntimeException {
    public UserSettingNotFoundException(String message) {
        super(message);
    }
}
