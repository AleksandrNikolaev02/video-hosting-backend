package com.example.business.config;

import com.example.business.handler.ClientResponseErrorHandler;
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
