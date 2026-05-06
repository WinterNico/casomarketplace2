package com.example.notificaciones.controller;

import com.example.notificaciones.dto.NotificacionRequest;
import com.example.notificaciones.model.Notificacion;
import com.example.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<Notificacion> enviarNotificacion(@Valid @RequestBody NotificacionRequest request) {
        // Al poner @Valid, Spring Boot revisa los @NotBlank antes de entrar aquí
        Notificacion guardada = notificacionService.enviarYGuardar(request);

        // Retornamos el objeto creado con código 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }
}