package com.example.email_service.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "topics")
public class TopicConfig {
    private String emailRequest;

    @Bean
    public NewTopic emailRequestTopic() {
        return TopicBuilder.name(emailRequest)
                .partitions(1)
                .build();
    }
}
