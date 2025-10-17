package com.example.dto;

public record PostMessageDTO(
        StatusProcessChannel status,
        String pipelineKey
) {
}
