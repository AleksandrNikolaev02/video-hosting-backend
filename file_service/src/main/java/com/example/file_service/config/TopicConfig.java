package com.example.file_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "topics")
@Configuration
@Getter
@Setter
public class TopicConfig {
    private String getFileReply;
    private String fileResponses;
    private String fileAnswers;
    private String createVideo;
    private String updatePath;
}
