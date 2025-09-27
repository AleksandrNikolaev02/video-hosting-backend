package com.example.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${app.web-client.base-url}")
    private String baseUrlWebClient;

    @Bean
    public WebClient webClient() {
        return WebClient.create(baseUrlWebClient);
    }
}
