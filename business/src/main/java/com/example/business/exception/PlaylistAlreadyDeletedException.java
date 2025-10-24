package com.example.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PlaylistAlreadyDeletedException extends RuntimeException {
    public PlaylistAlreadyDeletedException(String message) {
        super(message);
    }
}
