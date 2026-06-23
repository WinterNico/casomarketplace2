package com.example.busqueda.controller;

import com.example.busqueda.dto.OrderResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.busqueda.service.BusquedaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/busqueda")
@Tag(name = "Búsqueda (Agregador)", description = "Punto de consulta central que ensambla datos de Pedidos, Envíos y Usuarios")
public class BusquedaController {
    private final BusquedaService busquedaService;
    private static final Logger log = LoggerFactory.getLogger(BusquedaController.class);

    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalle completo del pedido",
            description = "Consulta a múltiples microservicios en paralelo para devolver un resumen consolidado con los datos del comprador, el estado del pago y el tracking de envío."
    )
    public ResponseEntity<OrderResponseDTO> obtenerPedidoCompleto(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        log.info("Petición REST recibida para buscar el detalle del pedido ID: {}", id);
        OrderResponseDTO respuesta = busquedaService.getDetalleCompleto(id, token).block();

        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}