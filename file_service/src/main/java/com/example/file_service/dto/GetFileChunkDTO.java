package com.example.file_service.dto;

public record GetFileChunkDTO(
        String filename,
        Long userId // id хозяина видео
) {
}
