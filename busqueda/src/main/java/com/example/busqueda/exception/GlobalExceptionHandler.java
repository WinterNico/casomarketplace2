package com.example.busqueda.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Unificacion en esta parte
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, String>> handleWebClientException(WebClientResponseException ex) {
        log.warn("Fallo de comunicación remota. Código: {} - Mensaje: {}", ex.getStatusCode(), ex.getStatusText());

        Map<String, String> response = new HashMap<>();
        response.put("error", "Error de comunicación con servicio externo: " + ex.getStatusText());

        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAll(Exception ex) {
        log.error("Fallo crítico interno en ms-busqueda: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", "Error interno en el servidor de búsquedas: " + ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}