package com.example.camunda.dto;

import com.example.camunda.enums.StatusMessage;

public record PostMessageDTO(
        StatusMessage status,
        String pipelineKey
) {
}
