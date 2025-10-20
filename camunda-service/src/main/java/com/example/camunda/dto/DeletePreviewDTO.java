package com.example.camunda.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeletePreviewDTO(
        @NotNull UUID filename
) {
}
