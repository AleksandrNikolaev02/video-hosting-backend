package com.example.api_gateway.filter;

import com.example.dto.AuthorizationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomGatewayFilter implements GatewayFilter {
    private final WebClient client;
    private final String authorizationHeader = "Authorization";

    public CustomGatewayFilter(WebClient client) {
        this.client = client;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return client.post()
                .uri("/token/validate")
                .header(authorizationHeader, getToken(exchange.getRequest()))
                .exchangeToMono(clientResponse -> {
                    log.info("Начало выполнения обработки ответа в Custom Gateway filter.");
                    if (clientResponse.statusCode().isError()) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    return clientResponse.bodyToMono(AuthorizationResponse.class)
                            .flatMap(authResponse -> proceedWithHeaders(exchange, chain, authResponse));
                });
    }

    private String getToken(ServerHttpRequest request) {
        return request.getHeaders().getFirst(authorizationHeader);
    }

    private Mono<Void> proceedWithHeaders(ServerWebExchange exchange, GatewayFilterChain chain, AuthorizationResponse response) {
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-user-role", response.getRole())
                .header("X-user-id", String.valueOf(response.getUserId()))
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }
}
