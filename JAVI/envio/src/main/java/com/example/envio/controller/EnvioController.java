package com.example.envio.controller;

import com.example.envio.dto.EnvioRequest;
import com.example.envio.model.Envio;
import com.example.envio.service.EnvioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    private static final Logger log = LoggerFactory.getLogger(EnvioController.class);
    private final EnvioService envioService;

    // cambio
    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> receivePedido(@Valid @RequestBody EnvioRequest request) {
        log.info("Pedido recibido para procesar envío. Order ID: {}", request.getId());
        envioService.createShipping(request.getId());

        // Devolvemos un JSON bonito
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Pedido recibido correctamente en el microservicio de envios");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Envio> getShippingByOrder(@PathVariable Long orderId) {
        log.info("Consultando estado de envío para la orden: {}", orderId);
        Envio envio = envioService.getShippingByOrder(orderId);
        return new ResponseEntity<>(envio, HttpStatus.OK);
    }
}