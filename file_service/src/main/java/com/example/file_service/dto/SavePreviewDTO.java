package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;

public record SavePreviewDTO(
        @NotNull String originalFilename
) {
}
