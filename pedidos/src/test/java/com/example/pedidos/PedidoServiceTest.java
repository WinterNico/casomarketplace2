package com.example.pedidos;

import com.example.pedidos.model.Pedido;
import com.example.pedidos.repository.PedidoRepository;
import com.example.pedidos.service.PedidoService;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedidoPrueba;
    private final String tokenFalso = "Bearer token_falso";

    @BeforeEach
    void setUp() {
        pedidoPrueba = new Pedido();
        pedidoPrueba.setId(1L);
        pedidoPrueba.setUserId(99L);
        pedidoPrueba.setTotal(new BigDecimal("150000.0"));

        when(webClientBuilder.build()).thenReturn(webClient);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    }


    @Test
    void createPedido_OrquestacionExitosa() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("email", "joker@phantom.cl")));
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OK"));

        Pedido resultado = pedidoService.createPedido(pedidoPrueba, tokenFalso, "1234567812345678");
        assertNotNull(resultado);
        verify(pedidoRepository, atLeastOnce()).save(any(Pedido.class));
    }


    @Test
    void createPedido_RechazadoPorExcepcionGenerica() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);
        when(responseSpec.bodyToMono(String.class)).thenThrow(new RuntimeException("Rechazado por Pagos"));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pedidoService.createPedido(pedidoPrueba, tokenFalso, "1111222233334444");
        });

        assertTrue(excepcion.getMessage().contains("No se pudo completar la compra"));
        assertEquals("RECHAZADO", pedidoPrueba.getState());
    }

    @Test
    void createPedido_FallaPorWebClientResponseException() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        WebClientResponseException errorHttp = WebClientResponseException.create(404, "Not Found", null, null, null);
        when(responseSpec.bodyToMono(Map.class)).thenThrow(errorHttp);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pedidoService.createPedido(pedidoPrueba, tokenFalso, "1234");
        });

        assertTrue(excepcion.getMessage().contains("Fallo en la orquestación (Servicio remoto)"));
        assertEquals("RECHAZADO", pedidoPrueba.getState());
    }

    @Test
    void createPedido_FallaPorWebClientRequestException() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        WebClientRequestException errorRed = new WebClientRequestException(new RuntimeException("Timeout"), HttpMethod.GET,
                URI.create("http://localhost"),
                new HttpHeaders());
        when(responseSpec.bodyToMono(Map.class)).thenThrow(errorRed);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pedidoService.createPedido(pedidoPrueba, tokenFalso, "1234");
        });

        assertTrue(excepcion.getMessage().contains("Fallo en la orquestación (Error de red)"));
        assertEquals("RECHAZADO", pedidoPrueba.getState());
    }


    @Test
    void createPedido_AplicaCompensacionReembolsado() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("email", "joker@phantom.cl")));

        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("OK"))
                .thenThrow(new RuntimeException("Caída del MS-Envios"));

        assertThrows(RuntimeException.class, () -> {
            pedidoService.createPedido(pedidoPrueba, tokenFalso, "1234");
        });

        assertEquals("REEMBOLSADO", pedidoPrueba.getState());
    }

    @Test
    void createPedido_FallaCompensacionErrorCritico() {
        when(pedidoRepository.save(any(Pedido.class)))
                .thenReturn(pedidoPrueba)
                .thenReturn(pedidoPrueba)
                .thenThrow(new RuntimeException("Base de datos desconectada"));

        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("email", "joker@phantom.cl")));

        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("OK"))
                .thenThrow(new RuntimeException("Caída del MS-Envios"));

        assertThrows(RuntimeException.class, () -> {
            pedidoService.createPedido(pedidoPrueba, tokenFalso, "1234");
        });

        assertEquals("ERROR_COMPENSACION", pedidoPrueba.getState());
    }



    @Test
    void getAllPedidos_DevuelveLista() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedidoPrueba));
        List<Pedido> resultado = pedidoService.getAllPedidos();
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void getPedidoById_Existente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPrueba));
        Optional<Pedido> resultado = pedidoService.getPedidoById(1L);
        assertTrue(resultado.isPresent());
        assertEquals(99L, resultado.get().getUserId());
    }

    @Test
    void getPedidosByUserId_DevuelveLista() {
        when(pedidoRepository.findByUserId(99L)).thenReturn(Arrays.asList(pedidoPrueba));
        List<Pedido> resultado = pedidoService.getPedidosByUserId(99L);
        assertEquals(1, resultado.size());
    }

    @Test
    void getPedidosByState_DevuelveLista() {
        when(pedidoRepository.findByState("PENDIENTE")).thenReturn(Arrays.asList(pedidoPrueba));
        List<Pedido> resultado = pedidoService.getPedidosByState("PENDIENTE");
        assertEquals(1, resultado.size());
    }

    @Test
    void deletePedido_Exitoso() {
        doNothing().when(pedidoRepository).deleteById(1L);
        pedidoService.deletePedido(1L);
        verify(pedidoRepository, times(1)).deleteById(1L);
    }


    @Test
    void updateState_Exitoso() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPrueba));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        Pedido resultado = pedidoService.updateState(1L, "ENVIADO");
        assertEquals("ENVIADO", resultado.getState());
    }

    @Test
    void updateState_LanzaErrorSiNoExiste() {
        when(pedidoRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pedidoService.updateState(2L, "ENVIADO");
        });

        assertEquals("Pedido no encontrado con el id: 2", excepcion.getMessage());
    }
}