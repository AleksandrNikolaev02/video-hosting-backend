package com.example.business.config;

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
    private String deleteDataChannel;
    private String publishEventTopic;

    @Bean
    public NewTopic createDeleteDataChannelTopic() {
        return TopicBuilder.name(deleteDataChannel)
                .partitions(2)
                .build();
    }
}
