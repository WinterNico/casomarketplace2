package com.example.envio;

import com.example.envio.model.Envio;
import com.example.envio.repository.EnvioRepository;
import com.example.envio.service.EnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioService envioService;

    private Envio envioFalsoBD;

    @BeforeEach
    void setUp() {
        envioFalsoBD = new Envio();
        envioFalsoBD.setId(10L);
        envioFalsoBD.setOrderId(1L);
        envioFalsoBD.setStatus("PREPARING");
        envioFalsoBD.setTrackingNumber("TRK-123456789");
    }


    @Test
    void createShipping_Exitoso() {
        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> {
            Envio envioGuardado = invocation.getArgument(0);
            envioGuardado.setId(10L);
            return envioGuardado;
        });

        Envio resultado = envioService.createShipping(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getOrderId());
        assertEquals("PREPARING", resultado.getStatus());

        assertTrue(resultado.getTrackingNumber().startsWith("TRK-"));

        verify(envioRepository, times(1)).save(any(Envio.class));
    }


    @Test
    void getShippingByOrder_Encontrado() {
        when(envioRepository.findByOrderId(1L)).thenReturn(Optional.of(envioFalsoBD));

        Envio resultado = envioService.getShippingByOrder(1L);

        assertNotNull(resultado);
        assertEquals("PREPARING", resultado.getStatus());
        assertEquals("TRK-123456789", resultado.getTrackingNumber());
    }


    @Test
    void getShippingByOrder_LanzaErrorSiNoExiste() {
        when(envioRepository.findByOrderId(99L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            envioService.getShippingByOrder(99L);
        });

        assertEquals("No se encontró información de envío para la orden ID: 99", excepcion.getMessage());
    }
}
