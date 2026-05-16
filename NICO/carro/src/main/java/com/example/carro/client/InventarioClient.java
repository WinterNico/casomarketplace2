package com.example.carro.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
public class InventarioClient {

    private final WebClient webClient;

    public InventarioClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9096/api/v1/inventory").build();
    }

    public Boolean checkStock(Long productId, Integer quantityRequired, String token) {
        Map response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{productId}")
                        .queryParam("quantityRequired", quantityRequired)
                        .build(productId))
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response != null && (Boolean) response.get("disponible");
    }
}