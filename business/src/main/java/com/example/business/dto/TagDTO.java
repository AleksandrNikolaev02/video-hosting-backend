package com.example.business.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record TagDTO(
        List<String> names,
        @NotNull UUID filename
) {
}
