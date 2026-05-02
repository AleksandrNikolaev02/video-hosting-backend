package com.example.auth_service.config;

import com.example.auth_service.handler.ClientResponseErrorHandler;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new ClientResponseErrorHandler();
    }
}
