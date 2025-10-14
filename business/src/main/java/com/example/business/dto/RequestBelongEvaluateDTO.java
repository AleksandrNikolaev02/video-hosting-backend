package com.example.business.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RequestBelongEvaluateDTO(
        @NotNull UUID filename
) {
}
