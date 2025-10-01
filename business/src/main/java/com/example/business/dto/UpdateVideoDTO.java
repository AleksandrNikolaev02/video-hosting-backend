package com.example.business.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateVideoDTO(
    @Size(min = 1, max = 100) @NotNull String title,
    @Size(min = 1, max = 5000) @NotNull String description
) {
}
