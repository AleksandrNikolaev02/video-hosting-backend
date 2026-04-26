package com.example.business.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateChannelDTO(
        @Size(min = 5) @NotNull String name,
        String description
) {
}
