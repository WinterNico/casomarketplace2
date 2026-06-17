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

    // Variables de prueba
    private NotificacionRequest requestPrueba;
    private Notificacion notificacionFalsaBD;

    @BeforeEach
    void setUp() {
        // Preparamos los datos de envío
        requestPrueba = new NotificacionRequest();
        requestPrueba.setEmailDestino("Duvaldaymenromero@gmail.com");
        requestPrueba.setAsunto("Confirmación de Compra");
        requestPrueba.setMensaje("Tu pago ha sido aprobado. Preparando el bajo para envío.");

        // Preparamos cómo se vería la notificación después de guardarse (con ID generado)
        notificacionFalsaBD = new Notificacion();
        notificacionFalsaBD.setId(10L);
        notificacionFalsaBD.setEmailDestino("Duvaldaymenromero@gmail.com");
        notificacionFalsaBD.setAsunto("Confirmación de Compra");
        notificacionFalsaBD.setMensaje("Tu pago ha sido aprobado. Preparando el bajo para envío.");
    }

    @Test
    void enviarYGuardar_Exitoso() {
        // "Cuando intenten guardar cualquier notificación, devuelve nuestra notificación falsa con ID 10"
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionFalsaBD);

        // Ejecutamos el servicio
        Notificacion resultado = notificacionService.enviarYGuardar(requestPrueba);

        // Comprobamos que todo salió perfecto
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId()); // Verificamos que Mockito le asignó el ID simulado
        assertEquals("Duvaldaymenromero@gmail.com", resultado.getEmailDestino());

        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }
}
