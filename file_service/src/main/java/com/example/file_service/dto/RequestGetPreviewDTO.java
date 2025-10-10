package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;

public record RequestGetPreviewDTO(
        @NotNull String filename
) {
}
