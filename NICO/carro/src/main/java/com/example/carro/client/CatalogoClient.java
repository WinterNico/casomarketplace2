package com.example.carro.client;

import com.example.carro.DTO.ProductoDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CatalogoClient {

    private final WebClient webClient;

    public CatalogoClient(WebClient.Builder webClientBuilder) {
        //CAMBIAR ESTO ADEMAS DEL APPLICATION.PROPERTIES EN EL CASO DE QUE UN PUERTO ESTE USADO, FLUCTUO ENTRE 8081/8084
        this.webClient = webClientBuilder.baseUrl("http://localhost:8084/api/catalogo").build();
    }

    public ProductoDTO getProducto(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(ProductoDTO.class)
                .block();
    }
}