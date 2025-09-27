package com.example.api_gateway.config;

import com.example.api_gateway.filter.CustomGatewayFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class ApiGatewayConfig {
    @Value("${app.service-name.app-service}")
    private String appServiceName;
    @Value("${app.service-name.auth-service}")
    private String authServiceName;
    private final CustomGatewayFilter customGatewayFilter;

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(predicateSpec ->
                    predicateSpec.path("/app/**")
                            .and()
                            .method(HttpMethod.GET, HttpMethod.DELETE, HttpMethod.POST)
                            .filters(filter -> filter.stripPrefix(1))
                            .uri(String.format("lb://%s", appServiceName)))
                // no auth endpoints
                .route(predicateSpec ->
                    predicateSpec.path("/noauth/**")
                            .filters(filter -> filter
                                    .stripPrefix(1))
                            .uri(String.format("lb://%s", authServiceName)))
                // auth endpoints
                .route(predicate -> predicate.path("/auth/**")
                        .filters(filter -> filter.filter(customGatewayFilter).stripPrefix(1))
                        .uri(String.format("lb://%s", appServiceName)))
                .build();
    }
}
