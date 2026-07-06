package com.example.pedidos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Atrapa los errores de validación del DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST); // 400
    }

    // NUEVO: Atrapa errores de respuesta de otros microservicios (Ej: si pagos devuelve 400)
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, String>> handleWebClientResponseException(WebClientResponseException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Error en servicio remoto: " + ex.getResponseBodyAsString());
        // Devuelve el mismo código HTTP que envió el otro microservicio
        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    // NUEVO: Atrapa errores de red o timeouts (Ej: el servicio de pagos está apagado o no responde)
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Map<String, String>> handleWebClientRequestException(WebClientRequestException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Fallo de comunicación con servicio externo: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE); // 503
    }

    // CORRECCIÓN: Atrapa errores generales de Runtime y los convierte en 500 (Internal Server Error)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}