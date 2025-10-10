package com.example.file_service.dto;

public record SavePreviewResponseDTO(
        String contentType,
        String filename
) {
}
