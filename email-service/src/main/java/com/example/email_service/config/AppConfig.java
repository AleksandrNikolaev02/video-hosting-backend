package com.example.email_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class AppConfig {
    @Value("${app.time-to-live-code}")
    private Integer timeToLiveCode;
    @Value("${app.min-code}")
    private Integer minCode;
    @Value("${app.max-code}")
    private Integer maxCode;
    @Value("${app.limit-to-delete}")
    private Integer limitToDelete;
}
