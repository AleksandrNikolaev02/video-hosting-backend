package com.example.auth_service.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestTokenRefreshDTO(
        @NotBlank(message = "Refresh token cannot be null!")
        String token) {
}
