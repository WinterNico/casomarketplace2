package com.example.envio.controller;

import com.example.envio.dto.EnvioRequest;
import com.example.envio.model.Envio;
import com.example.envio.service.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Envíos", description = "Gestión de la logística, preparación y seguimiento de pedidos")
public class EnvioController {

    private static final Logger log = LoggerFactory.getLogger(EnvioController.class);
    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping
    @Operation(
            summary = "Procesar nuevo envío",
            description = "Recibe el ID de una orden ya pagada y genera un registro de envío con estado PREPARING y un número de seguimiento único."
    )
    public ResponseEntity<Map<String, String>> receivePedido(@Valid @RequestBody EnvioRequest request) {
        log.info("Pedido recibido para procesar envío. Order ID: {}", request.getId());
        envioService.createShipping(request.getId());

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Pedido recibido correctamente en el microservicio de envios");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    @Operation(
            summary = "Consultar envío por Orden",
            description = "Busca y devuelve los detalles del envío (incluyendo el Tracking Number) asociado al ID de un pedido."
    )
    public ResponseEntity<Envio> getShippingByOrder(@PathVariable Long orderId) {
        log.info("Consultando estado de envío para la orden: {}", orderId);
        Envio envio = envioService.getShippingByOrder(orderId);
        return new ResponseEntity<>(envio, HttpStatus.OK);
    }
}