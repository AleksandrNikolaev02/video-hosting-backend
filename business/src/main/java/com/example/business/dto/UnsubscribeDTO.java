package com.example.business.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UnsubscribeDTO(
        @NotNull @Positive Long channelId
) {
}
