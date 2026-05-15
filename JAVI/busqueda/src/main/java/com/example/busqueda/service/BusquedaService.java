package com.example.busqueda.service;

import com.example.busqueda.dto.OrderResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class BusquedaService {
    private final WebClient.Builder webClientBuilder;

    public BusquedaService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<OrderResponseDTO> getDetalleCompleto(Long id) {
        WebClient pedidoClient = webClientBuilder.baseUrl("http://localhost:8082/api/v1/pedidos").build();
        WebClient envioClient = webClientBuilder.baseUrl("http://localhost:8083/api/v1/envios").build();

        return Mono.zip(
                pedidoClient.get().uri("/{id}", id).retrieve().bodyToMono(Map.class),
                envioClient.get().uri("/order/{id}", id).retrieve().bodyToMono(Map.class)
        ).map(tuple -> {
            Map pedido = tuple.getT1();
            Map envio = tuple.getT2();

            return new OrderResponseDTO(
                    Long.valueOf(pedido.get("id").toString()),
                    Long.valueOf(pedido.get("userId").toString()),
                    pedido.get("state").toString(),
                    new java.math.BigDecimal(pedido.get("total").toString()),
                    java.time.LocalDateTime.parse(pedido.get("creationDate").toString()),
                    envio.get("status").toString(),
                    envio.get("trackingNumber").toString()
            );
        });
    }
}
