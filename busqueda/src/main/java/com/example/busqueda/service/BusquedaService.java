package com.example.busqueda.service;

import com.example.busqueda.dto.OrderResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class BusquedaService {

    private static final Logger log = LoggerFactory.getLogger(BusquedaService.class);
    private final WebClient.Builder webClientBuilder;

    public BusquedaService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    // Fíjate que ahora recibe el token
    public Mono<OrderResponseDTO> getDetalleCompleto(Long id, String token) {
        log.info("Iniciando flujo de búsqueda para el pedido ID: {}", id);

        WebClient pedidoClient = webClientBuilder.baseUrl("http://pedidos/api/v1/pedidos").build();
        WebClient envioClient = webClientBuilder.baseUrl("http://envios/api/v1/envios").build();
        WebClient usuarioClient = webClientBuilder.baseUrl("http://usuarios/api/v1/usuarios").build();

        // Buscamos el pedido y pasamos token
        return pedidoClient.get().uri("/{id}", id)
                .header("Authorization", token) // <--- TOKEN AQUÍ
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(pedido -> {
                    Long userId = Long.valueOf(pedido.get("userId").toString());
                    log.info("Pedido encontrado. El dueño es el Usuario ID: {}. Solicitando datos en paralelo...", userId);

                    // Buscamos Envíos y Usuarios, token tmb
                    return Mono.zip(
                            envioClient.get().uri("/order/{id}", id)
                                    .header("Authorization", token)
                                    .retrieve().bodyToMono(Map.class),
                            usuarioClient.get().uri("/buscar/{id}", userId)
                                    .header("Authorization", token)
                                    .retrieve().bodyToMono(Map.class)
                    ).map(tuple -> {
                        Map envio = tuple.getT1();
                        Map usuario = tuple.getT2();

                        // Ensamblamos todo
                        return new OrderResponseDTO(
                                Long.valueOf(pedido.get("id").toString()),
                                userId,
                                usuario.get("name").toString(),
                                usuario.get("email").toString(),
                                pedido.get("state").toString(),
                                new java.math.BigDecimal(pedido.get("total").toString()),
                                java.time.LocalDateTime.parse(pedido.get("creationDate").toString()),
                                envio.get("status").toString(),
                                envio.get("trackingNumber").toString()
                        );
                    });
                })
                .doOnSuccess(dto -> log.info("JSON ensamblado con éxito para el pedido ID: {}", id))
                .doOnError(error -> log.error("Fallo la agregación de datos: {}", error.getMessage()));
    }
}