package com.example.busqueda.controller;


import com.example.busqueda.dto.OrderResponseDTO;
import com.example.busqueda.service.BusquedaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/busqueda")
public class BusquedaController {
    private final BusquedaService busquedaService;

    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @GetMapping("/{id}")
    public Mono<OrderResponseDTO> obtenerPedidoCompleto(@PathVariable Long id) {
        return busquedaService.getDetalleCompleto(id);
    }
}
