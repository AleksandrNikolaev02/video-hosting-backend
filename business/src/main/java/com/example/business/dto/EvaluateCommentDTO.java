package com.example.business.dto;

import com.example.business.enums.EvaluateType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EvaluateCommentDTO(
        @NotNull @Positive Long commentId,
        @NotNull EvaluateType evaluateType
) {
}
