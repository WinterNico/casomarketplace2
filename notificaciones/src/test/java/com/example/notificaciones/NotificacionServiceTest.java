package com.example.notificaciones;

import com.example.notificaciones.dto.NotificacionRequest;
import com.example.notificaciones.model.Notificacion;
import com.example.notificaciones.repository.NotificacionRepository;
import com.example.notificaciones.service.NotificacionService;
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
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private NotificacionRequest requestPrueba;
    private Notificacion notificacionFalsaBD;

    @BeforeEach
    void setUp() {
        requestPrueba = new NotificacionRequest();
        requestPrueba.setEmailDestino("Duvaldaymenromero@gmail.com");
        requestPrueba.setAsunto("Confirmación de Compra");
        requestPrueba.setMensaje("Tu pago ha sido aprobado. Preparando el bajo para envío.");

        notificacionFalsaBD = new Notificacion();
        notificacionFalsaBD.setId(10L);
        notificacionFalsaBD.setEmailDestino("Duvaldaymenromero@gmail.com");
        notificacionFalsaBD.setAsunto("Confirmación de Compra");
        notificacionFalsaBD.setMensaje("Tu pago ha sido aprobado. Preparando el bajo para envío.");
    }

    @Test
    void enviarYGuardar_Exitoso() {
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionFalsaBD);

        Notificacion resultado = notificacionService.enviarYGuardar(requestPrueba);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Duvaldaymenromero@gmail.com", resultado.getEmailDestino());

        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }
}
