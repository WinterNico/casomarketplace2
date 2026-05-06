package com.example.notificaciones.service;

import com.example.notificaciones.dto.NotificacionRequest;
import com.example.notificaciones.model.Notificacion;
import com.example.notificaciones.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    // Instanciamos el Logger (SLF4J)
    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Notificacion enviarYGuardar(NotificacionRequest request) {
        log.info("Iniciando proceso de envío de notificación a: {}", request.getEmailDestino());

        // 1. Convertimos el DTO a Entidad
        Notificacion nuevaNotificacion = new Notificacion();
        nuevaNotificacion.setEmailDestino(request.getEmailDestino());
        nuevaNotificacion.setAsunto(request.getAsunto());
        nuevaNotificacion.setMensaje(request.getMensaje());

        // 2. Guardamos en la Base de Datos (Persistencia Real)
        Notificacion notificacionGuardada = notificacionRepository.save(nuevaNotificacion);

        log.info("Notificación guardada en BD con ID: {}", notificacionGuardada.getId());

        // 3. Simulamos el envío
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("Error en la simulación de envío: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }

        log.info("Notificación ENTREGADA con éxito a {}", request.getEmailDestino());

        return notificacionGuardada;
    }
}