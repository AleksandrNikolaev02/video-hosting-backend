package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;

public record DeletePreviewDTO(
    @NotNull String filename
) {
}
