package com.example.notificaciones.controller;

import com.example.notificaciones.dto.NotificacionRequest;
import com.example.notificaciones.model.Notificacion;
import com.example.notificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/notificaciones")
@Tag(name = "Notificaciones", description = "Gestión y envío de correos electrónicos del sistema")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @PostMapping("/enviar")
    @Operation(
            summary = "Enviar Notificación",
            description = "Guarda el registro en base de datos y simula el envío del correo electrónico al destinatario indicado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notificacion registrada y enviada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los datos enviados (email inválido, otro)")
    })
    public ResponseEntity<Notificacion> enviarNotificacion(@Valid @RequestBody NotificacionRequest request) {
        Notificacion guardada = notificacionService.enviarYGuardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }
}