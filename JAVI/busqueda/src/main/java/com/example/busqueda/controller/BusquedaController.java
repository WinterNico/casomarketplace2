package com.example.busqueda.controller;


import com.example.busqueda.dto.OrderResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.busqueda.service.BusquedaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/busqueda")
public class BusquedaController {
    private final BusquedaService busquedaService;
    private static final Logger log = LoggerFactory.getLogger(BusquedaController.class);

    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> obtenerPedidoCompleto(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) { // <--- ATRAPAMOS EL TOKEN AQUÍ

        log.info("Petición REST recibida para buscar el detalle del pedido ID: {}", id);
        OrderResponseDTO respuesta = busquedaService.getDetalleCompleto(id, token).block();

        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}
