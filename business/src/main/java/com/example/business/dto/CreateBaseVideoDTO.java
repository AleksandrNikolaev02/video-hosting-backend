package com.example.business.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBaseVideoDTO(
        @NotNull String title,
        @NotNull String description,
        @NotNull UUID filename
) {
}
