package com.example.catalogo.controller;

import com.example.catalogo.model.Producto;
import com.example.catalogo.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = service.crearProducto(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return new ResponseEntity<>(service.obtenerTodos(), HttpStatus.OK);
    }
}
