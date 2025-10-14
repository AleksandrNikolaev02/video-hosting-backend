package com.example.business.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record UpdatePreviewDTO(
        @NotNull UUID filename,
        @NotNull String path
) {
}
