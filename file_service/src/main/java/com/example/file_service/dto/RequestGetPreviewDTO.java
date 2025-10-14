package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RequestGetPreviewDTO(
        @NotNull UUID filename
) {
}
