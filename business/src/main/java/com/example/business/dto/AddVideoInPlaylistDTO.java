package com.example.business.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AddVideoInPlaylistDTO(
        @NotNull UUID filename,
        @NotNull @Positive Long playlistId
) {
}
