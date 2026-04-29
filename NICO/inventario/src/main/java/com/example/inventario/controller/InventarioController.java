package com.example.inventario.controller;

import com.example.inventario.model.Inventario;
import com.example.inventario.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventarioController {
    @Autowired
    private InventarioService inventarioService;
    @GetMapping("/{productId}")
    public Boolean checkStock(@PathVariable Long productId, @RequestParam Integer quantityRequired) {
        return inventarioService.checkStock(productId, quantityRequired);
    }
    @PostMapping("/add")
    public ResponseEntity<Inventario> addStock(@RequestBody Inventario inventory) {
        return new ResponseEntity<>(inventarioService.addStock(inventory), HttpStatus.CREATED);
    }
}
