package com.bidwise.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    private final WebClient.Builder webClientBuilder;

    public GatewayConfig(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/hello")
                        .uri("lb://core"))
                .route(p -> p
                        .path("/api/v1/demo-controller")
                        .uri("http://localhost:8081/api/v1/demo-controller"))
                .route(p -> p
                        .path("/api/v1/auth/register")
                        .uri("http://localhost:8081/api/v1/auth/register"))
                .route(p -> p
                        .path("/")
                        .filters(f -> f.filter(new AuthenticationFilter(webClientBuilder)))
                        .uri("http://localhost:8083/"))
                .build();
    }

    public static class AuthenticationFilter implements GatewayFilter {

        private final WebClient.Builder webClientBuilder;

        public AuthenticationFilter(WebClient.Builder webClientBuilder) {
            this.webClientBuilder = webClientBuilder;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/v1/auth/check-auth")
                    .headers(httpHeaders -> {
                        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
                        if (authorizationHeader != null) {
                            httpHeaders.add("Authorization", "Bearer " + extractToken(authorizationHeader));
                        }
                    })
                    .exchangeToMono(this::handleResponse)
                    .flatMap(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return chain.filter(exchange);
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }
                    });


        }

        private Mono<ClientResponse> handleResponse(ClientResponse responseMono) {
            return Mono.just(responseMono);
        }

        private String extractToken(String authorizationHeader) {
            return authorizationHeader.substring("Bearer ".length());
        }
    }
}
