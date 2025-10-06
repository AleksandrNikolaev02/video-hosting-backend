package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePreviewDTO(
        @NotNull String filename,
        @NotNull String originalFilename
) {
}
