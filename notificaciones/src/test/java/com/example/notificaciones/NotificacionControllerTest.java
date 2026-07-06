package com.example.notificaciones;

import com.example.notificaciones.controller.NotificacionController;
import com.example.notificaciones.dto.NotificacionRequest;
import com.example.notificaciones.model.Notificacion;
import com.example.notificaciones.service.NotificacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private NotificacionController notificacionController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificacionController).build();
    }

    @Test
    void enviarNotificacion_Exitoso() throws Exception {
        NotificacionRequest request = new NotificacionRequest();
        request.setEmailDestino("correo@fantasma.com");
        request.setAsunto("Prueba");
        request.setMensaje("Mensaje de prueba");

        Notificacion notificacionFalsa = new Notificacion();
        notificacionFalsa.setId(1L);
        notificacionFalsa.setEmailDestino("correo@fantasma.com");

        when(notificacionService.enviarYGuardar(any(NotificacionRequest.class))).thenReturn(notificacionFalsa);

        mockMvc.perform(post("/api/v1/notificaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.emailDestino").value("correo@fantasma.com"));
    }
}