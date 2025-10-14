package com.example.file_service.dto;

import java.util.UUID;

public record SavePreviewResponseDTO(
        String contentType,
        UUID filename
) {
}
