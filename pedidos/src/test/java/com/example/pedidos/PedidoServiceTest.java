package com.example.pedidos;

import com.example.pedidos.model.Pedido;
import com.example.pedidos.repository.PedidoRepository;
import com.example.pedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedidoPrueba;
    private String tokenFalso = "Bearer token_falso";

    @BeforeEach
    void setUp() {
        pedidoPrueba = new Pedido();
        pedidoPrueba.setId(1L);
        pedidoPrueba.setUserId(1L);
        pedidoPrueba.setTotal(new BigDecimal("150000.0"));

        // Simulación genérica del comportamiento del WebClient para TODAS las llamadas (Pagos, Envíos, Notis)
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.post()).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void createPedido_OrquestacionExitosa() {
        // GIVEN: El guion
        // Al guardar por primera vez (PROCESANDO) y segunda vez (PAGADO), devuelve el pedido
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        // Simula que TODAS las respuestas de los otros microservicios dicen "OK"
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OK"));

        // WHEN: Ejecuta la orquestación completa
        Pedido resultado = pedidoService.createPedido(pedidoPrueba, tokenFalso, "1234567812345678");

        // THEN: Comprobamos el éxito
        assertNotNull(resultado);
        assertEquals("PAGADO", resultado.getState());

        // El repositorio debió ser llamado 2 veces: Una para crear (PROCESANDO) y otra para actualizar a PAGADO
        verify(pedidoRepository, times(2)).save(any(Pedido.class));
    }

    @Test
    void createPedido_RechazadoPorErrorDePago() {
        // GIVEN: El guion del camino triste
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        // Simulamos que el WebClient tira una excepción (ej: Tarjeta sin fondos)
        when(responseSpec.bodyToMono(String.class)).thenThrow(new RuntimeException("Rechazado por Pagos"));

        // WHEN & THEN: Esperamos que el servicio reviente y ataje el error
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pedidoService.createPedido(pedidoPrueba, tokenFalso, "1111222233334444");
        });

        assertTrue(excepcion.getMessage().contains("No se pudo completar la compra"));

        // Verificamos que el pedido quedó como RECHAZADO
        assertEquals("RECHAZADO", pedidoPrueba.getState());
    }

    @Test
    void updateState_Exitoso() {
        // GIVEN
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPrueba));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        // WHEN
        Pedido resultado = pedidoService.updateState(1L, "ENVIADO");

        // THEN
        assertEquals("ENVIADO", resultado.getState());
    }
}
