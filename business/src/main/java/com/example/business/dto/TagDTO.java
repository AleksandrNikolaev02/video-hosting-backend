package com.example.business.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record TagDTO(
        List<String> names,
        @Positive @NotNull Long videoId
) {
}
