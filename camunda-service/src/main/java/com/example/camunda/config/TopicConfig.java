package com.example.camunda.config;

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
    private String publishEventTopic;
    private String compensatingTransactionBusinessService;

    @Bean
    public NewTopic createPublishEvent() {
        return TopicBuilder.name(publishEventTopic)
                           .partitions(1)
                           .build();
    }

    @Bean
    public NewTopic createCompensatingTransactionEvent() {
        return TopicBuilder.name(compensatingTransactionBusinessService)
                           .partitions(2)
                           .build();
    }
}
