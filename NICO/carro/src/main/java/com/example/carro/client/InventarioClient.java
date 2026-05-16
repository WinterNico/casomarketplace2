package com.example.carro.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class InventarioClient {

    private final WebClient webClient;

    public InventarioClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082/api/inventory").build();
    }

    public Boolean checkStock(Long productId, Integer quantityRequired) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{productId}")
                        .queryParam("quantityRequired", quantityRequired)
                        .build(productId))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}