package com.example.auth_service.dto;

import lombok.Builder;

@Builder
public record ResponseTokenRefreshDTO(String accessToken, String refreshToken) {
}