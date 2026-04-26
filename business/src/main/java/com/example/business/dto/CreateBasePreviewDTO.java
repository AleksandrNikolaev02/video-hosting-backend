package com.example.business.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBasePreviewDTO(
        @NotNull UUID videoId
) {
}
