package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SavePreviewDTO(
        @NotNull String originalFilename,
        @NotNull UUID filename
        ) {
}
