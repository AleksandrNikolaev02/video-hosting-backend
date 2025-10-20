package com.example.camunda.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "params")
public class ParametersConfig {
    private String userRole;
    private String userId;
    private String namePipelineKey;
    private Integer countSuccessRequest;
    private String countSuccessRequestName;
    private String successDeleteData;
    private String nameChannelId;
}
