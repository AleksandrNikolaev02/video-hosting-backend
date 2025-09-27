package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record SaveChunksDTO(
    @NotNull @Positive Long userId,
    @NotNull @Positive Long key
) {
}
