package com.example.business.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCommentDTO(
        @NotNull @Size(min = 1) String content,
        @NotNull UUID videoId,
        Long commentId
) {
}
