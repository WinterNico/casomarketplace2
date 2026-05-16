package com.example.carro.client;

import com.example.carro.DTO.ProductoDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CatalogoClient {

    private final WebClient webClient;

    public CatalogoClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9095/api/v1/catalogo").build();
    }

    public ProductoDTO getProducto(Long id, String token) {
        return webClient.get()
                .uri("/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(ProductoDTO.class)
                .block();
    }
}