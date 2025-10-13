package com.example.business.dto;

import jakarta.validation.constraints.NotNull;

public record CreatePlaylistDTO(
        @NotNull String name
) {
}
