package com.example.business.dto;

import com.example.business.enums.RequestChannelStatus;
import jakarta.validation.constraints.NotNull;

public record SendRequestDTO(
        @NotNull RequestChannelStatus status
) {
}
