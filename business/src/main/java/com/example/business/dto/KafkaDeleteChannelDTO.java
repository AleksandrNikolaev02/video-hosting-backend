package com.example.business.dto;

public record KafkaDeleteChannelDTO(
        Long channelId,
        String pipelineKey
) {
}
