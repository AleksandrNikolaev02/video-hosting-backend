package com.example.business.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EditCommentDTO(
        @NotNull @NotEmpty String content,
        @NotNull @Positive Long commentId
) {
}
