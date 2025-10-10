package com.example.file_service.dto;

import lombok.Builder;

@Builder
public record GetPreviewDTO(
        byte[] data,
        String contentType
) {
}
