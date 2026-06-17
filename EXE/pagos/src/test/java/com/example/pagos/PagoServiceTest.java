package com.example.pagos;

import com.example.pagos.dto.PagoRequest;
import com.example.pagos.dto.PagoResponse;
import com.example.pagos.model.Pago;
import com.example.pagos.repository.PagoRepository;
import com.example.pagos.service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoService pagoService;

    private PagoRequest pagoAprobadoRequest;
    private PagoRequest pagoRechazadoRequest;

    @BeforeEach
    void setUp() {
        // Compra normal de un bajo eléctrico
        pagoAprobadoRequest = new PagoRequest();
        pagoAprobadoRequest.setIdPedido(1L);
        pagoAprobadoRequest.setMonto(150000.0);
        pagoAprobadoRequest.setNumeroTarjeta("1234567812345678"); // Tarjeta buena

        // Compra que debe fallar
        pagoRechazadoRequest = new PagoRequest();
        pagoRechazadoRequest.setIdPedido(2L);
        pagoRechazadoRequest.setMonto(50000.0);
        pagoRechazadoRequest.setNumeroTarjeta("1111222233334444"); // La tarjeta mala
    }

    @Test
    void procesarPago_Aprobado() {
        // "Cuando el servicio guarde el pago, no hagas nada especial, solo devuelve el objeto"
        when(pagoRepository.save(any(Pago.class))).thenReturn(new Pago());

        PagoResponse respuesta = pagoService.procesarPago(pagoAprobadoRequest);

        assertNotNull(respuesta);
        assertEquals("APROBADO", respuesta.getEstado());
        assertEquals("Pago procesado correctamente", respuesta.getMensaje());
        assertNotNull(respuesta.getTransaccionId()); // Verificamos que se generó un código TXN-

        // Verificamos que se guardó en BD
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void procesarPago_RechazadoPorFondosInsuficientes() {
        when(pagoRepository.save(any(Pago.class))).thenReturn(new Pago());

        PagoResponse respuesta = pagoService.procesarPago(pagoRechazadoRequest);

        assertNotNull(respuesta);
        assertEquals("RECHAZADO", respuesta.getEstado());
        assertEquals("Fondos insuficientes o tarjeta bloqueada", respuesta.getMensaje());

        verify(pagoRepository, times(1)).save(any(Pago.class));
    }
}