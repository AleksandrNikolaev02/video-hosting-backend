package com.example.business.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeletePreviewDTO(
        @NotNull UUID filename
) {
}
