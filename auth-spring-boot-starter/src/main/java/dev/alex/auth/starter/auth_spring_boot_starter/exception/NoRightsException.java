package dev.alex.auth.starter.auth_spring_boot_starter.exception;

public class NoRightsException extends RuntimeException {
    public NoRightsException(String message) {
        super(message);
    }
}
