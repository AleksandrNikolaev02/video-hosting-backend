package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SaveChunksDTO(
    @NotNull @Positive Long userId,
    @NotNull @Size(min = 1) String key
) {
}
