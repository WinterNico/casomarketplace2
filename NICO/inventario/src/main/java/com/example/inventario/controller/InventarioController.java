package com.example.inventario.controller;

import com.example.inventario.model.Inventario;
import com.example.inventario.service.InventarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventarioController {

    private static final Logger log = LoggerFactory.getLogger(InventarioController.class);

    @Autowired
    private InventarioService inventarioService;

    // Ahora devolvemos un Map para que se transforme en un JSON bonito
    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkStock(
            @PathVariable Long productId,
            @RequestParam Integer quantityRequired) {

        log.info("Consultando stock para Producto ID: {}, Cantidad requerida: {}", productId, quantityRequired);
        boolean isAvailable = inventarioService.checkStock(productId, quantityRequired);

        Map<String, Boolean> response = new HashMap<>();
        response.put("disponible", isAvailable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Inventario> addStock(@RequestBody Inventario inventory) {
        log.info("Ingresando stock a la bodega. Producto ID: {}, Cantidad: {}", inventory.getProductId(), inventory.getQuantity());

        Inventario saved = inventarioService.addStock(inventory);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}