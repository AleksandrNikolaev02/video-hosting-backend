package com.example.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record RegisterDTO(
        @NotNull(message = "First name cannot be null")
        String firstName,
        @NotNull(message = "Second name cannot be null")
        String secondName,
        @NotNull(message = "Email cannot be null")
        @Email(message = "Email should be valid")
        String email,
        @NotNull(message = "Password cannot be null")
        @Size(min = 4, message = "Password should have at least 4 characters")
        String password
) implements Serializable {
}
