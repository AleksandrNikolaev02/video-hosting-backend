package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SavePreviewDTO(
        @NotNull byte[] data,
        @NotNull String contentType,
        @Positive long contentLength
) {
}
