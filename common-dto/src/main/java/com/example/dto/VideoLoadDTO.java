package com.example.dto;

import lombok.Builder;

@Builder
public record VideoLoadDTO(
        byte[] data,
        String contentType,
        String partName,
        Long key
) {
}
