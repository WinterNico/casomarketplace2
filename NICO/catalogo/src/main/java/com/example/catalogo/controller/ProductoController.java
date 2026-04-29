package com.example.catalogo.controller;

import com.example.catalogo.model.Producto;
import com.example.catalogo.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/catalogo")
public class ProductoController {
    @Autowired
    private ProductoService service;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProducto(@PathVariable Long id){
        Optional<Producto> producto = service.getProductoById(id);
        if (producto.isPresent()){
            return new ResponseEntity<>(producto.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("producto no encontrado", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/add")
    public ResponseEntity<Producto> addProducto(@RequestBody Producto producto){
        return new ResponseEntity<>(service.addProducto(producto), HttpStatus.CREATED);
    }
}


