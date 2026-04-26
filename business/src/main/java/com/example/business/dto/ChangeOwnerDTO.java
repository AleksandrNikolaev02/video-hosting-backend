package com.example.business.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChangeOwnerDTO(
        @NotNull @Positive Long channelId,
        @NotNull @Positive Long newOwnerId
) {
}
