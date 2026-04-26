package com.example.camunda.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "tasks")
public class CamundaConfig {
    private String instanceKey;
    private String businessKey;
}
