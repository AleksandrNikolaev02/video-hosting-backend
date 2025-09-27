package com.example.auth_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "topics")
@Getter
@Setter
public class TopicConfig {
    private String emailRequest;
    private String saveUser;
    private String createUser;
    private String getEmail;
}
