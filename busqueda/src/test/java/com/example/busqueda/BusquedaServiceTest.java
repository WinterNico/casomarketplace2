package com.example.busqueda;



import com.example.busqueda.dto.OrderResponseDTO;
import com.example.busqueda.service.BusquedaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusquedaServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private BusquedaService busquedaService;

    private Map<String, Object> megaMapaFalso;

    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        megaMapaFalso = new HashMap<>();
        megaMapaFalso.put("id", 1);
        megaMapaFalso.put("userId", 100);
        megaMapaFalso.put("state", "PAGADO");
        megaMapaFalso.put("total", "150000.0");
        megaMapaFalso.put("creationDate", "2026-06-17T12:00:00");
        megaMapaFalso.put("status", "PREPARING");
        megaMapaFalso.put("trackingNumber", "TRK-987654321");
        megaMapaFalso.put("name", "Exequiel");
        megaMapaFalso.put("email", "Duvaldaymenromero@gmail.com");
    }

    @Test
    void getDetalleCompleto_Exitoso() {
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(megaMapaFalso));

        OrderResponseDTO resultado = busquedaService.getDetalleCompleto(1L, "Bearer tokenFalso").block();

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(100L, resultado.getUserId());
        assertEquals("Exequiel", resultado.getUserName());
        assertEquals("Duvaldaymenromero@gmail.com", resultado.getUserEmail());
        assertEquals(new BigDecimal("150000.0"), resultado.getTotal());
        assertEquals("PAGADO", resultado.getState());
        assertEquals("PREPARING", resultado.getStatus());
        assertEquals("TRK-987654321", resultado.getTrackingNumber());
    }
}