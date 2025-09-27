package com.example.file_service.dto;

public record ChunkFileDTO(
        byte[] data,
        String contentType,
        long start,
        long end,
        long fileLength
) {
}
