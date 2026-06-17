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
        envioFalsoBD.setId(10L); // Suponiendo que tu entidad tiene setId
        envioFalsoBD.setOrderId(1L);
        envioFalsoBD.setStatus("PREPARING");
        envioFalsoBD.setTrackingNumber("TRK-123456789");
    }

    // -------------------------------------------------------------------
    // TEST 1: Creación de envío (El camino feliz)
    // -------------------------------------------------------------------
    @Test
    void createShipping_Exitoso() {
        // GIVEN: "Cuando el repositorio intente guardar, devuelve el objeto simulado"
        // Como tu método createShipping retorna lo que devuelve el save(), probamos esa salida.
        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> {
            Envio envioGuardado = invocation.getArgument(0);
            envioGuardado.setId(10L); // Simulamos que la BD le asignó un ID
            return envioGuardado;
        });

        // WHEN: Ejecutamos el servicio para la orden 1
        Envio resultado = envioService.createShipping(1L);

        // THEN: Comprobamos que la lógica interna de tu Service funcionó
        assertNotNull(resultado);
        assertEquals(1L, resultado.getOrderId());
        assertEquals("PREPARING", resultado.getStatus());

        // Verificamos que el Tracking Number se generó correctamente (empieza con TRK-)
        assertTrue(resultado.getTrackingNumber().startsWith("TRK-"));

        // Comprobamos que el repositorio fue llamado exactamente una vez
        verify(envioRepository, times(1)).save(any(Envio.class));
    }

    // -------------------------------------------------------------------
    // TEST 2: Búsqueda exitosa
    // -------------------------------------------------------------------
    @Test
    void getShippingByOrder_Encontrado() {
        // GIVEN
        when(envioRepository.findByOrderId(1L)).thenReturn(Optional.of(envioFalsoBD));

        // WHEN
        Envio resultado = envioService.getShippingByOrder(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals("PREPARING", resultado.getStatus());
        assertEquals("TRK-123456789", resultado.getTrackingNumber());
    }

    // -------------------------------------------------------------------
    // TEST 3: Búsqueda fallida (Lanza Excepción)
    // -------------------------------------------------------------------
    @Test
    void getShippingByOrder_LanzaErrorSiNoExiste() {
        // GIVEN: "Cuando busquen la orden 99, devuelve vacío"
        when(envioRepository.findByOrderId(99L)).thenReturn(Optional.empty());

        // WHEN & THEN: Verificamos que tu servicio se defiende lanzando el error exacto
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            envioService.getShippingByOrder(99L);
        });

        assertEquals("No se encontró información de envío para la orden ID: 99", excepcion.getMessage());
    }
}
