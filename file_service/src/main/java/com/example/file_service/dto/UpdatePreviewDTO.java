package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdatePreviewDTO(
        @NotNull UUID filename,
        @NotNull String originalFilename
) {
}
