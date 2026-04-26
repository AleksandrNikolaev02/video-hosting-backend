package com.example.business.dto;

import jakarta.validation.constraints.NotNull;

public record CreateBaseVideoDTO(
        @NotNull String title,
        @NotNull String description
) {
}
