package com.example.api_gateway.config;

import com.example.api_gateway.filter.CustomGatewayFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApiGatewayConfig {
    @Value("${app.service-name.auth-service}")
    private String authServiceName;
    @Value("${app.service-name.file-service}")
    private String fileServiceName;
    @Value("${app.service-name.business-service}")
    private String businessServiceName;
    private final CustomGatewayFilter customGatewayFilter;

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // no auth endpoints
                .route(predicateSpec ->
                        predicateSpec.path("/noauth/file-service/**")
                                .filters(filter -> filter.stripPrefix(2))
                                .uri(String.format("lb://%s", fileServiceName)))
                .route(predicateSpec ->
                        predicateSpec.path("/noauth/business-service/**")
                                .filters(filter -> filter.stripPrefix(2))
                                .uri(String.format("lb://%s", businessServiceName)))
                .route(predicateSpec ->
                        predicateSpec.path("/noauth/auth-service/**")
                                .filters(filter -> filter.stripPrefix(2))
                                .uri(String.format("lb://%s", authServiceName)))
                .route(predicate -> predicate.path("/auth/file-service/**")
                        .filters(filter -> filter.filter(customGatewayFilter).stripPrefix(2))
                        .uri(String.format("lb://%s", fileServiceName)))
                .route(predicate -> predicate.path("/auth/business-service/**")
                        .filters(filter -> filter.filter(customGatewayFilter).stripPrefix(2))
                        .uri(String.format("lb://%s", businessServiceName)))
                .build();
    }
}
