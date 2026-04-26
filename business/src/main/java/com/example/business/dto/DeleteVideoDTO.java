package com.example.business.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteVideoDTO(
        @NotNull UUID filename
) {
}
