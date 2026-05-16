package com.example.catalogo.controller;

import com.example.catalogo.model.Producto;
import com.example.catalogo.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/catalogo")
public class ProductoController {
    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService service;

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProducto(@PathVariable Long id){
        log.info("Petición recibida para buscar producto ID: {}", id);
        Optional<Producto> producto = service.getProductoById(id);

        if (producto.isPresent()){
            log.info("Producto ID {} encontrado con éxito.", id);
            return new ResponseEntity<>(producto.get(), HttpStatus.OK);
        } else {
            log.warn("El producto ID {} no existe en la base de datos.", id);
            throw new RuntimeException("Producto no encontrado");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Producto> addProducto(@RequestBody Producto producto){
        log.info("Petición recibida para agregar nuevo producto: {}", producto.getNombre());
        Producto nuevoProducto = service.addProducto(producto);
        log.info("Producto {} guardado con el ID: {}", nuevoProducto.getNombre(), nuevoProducto.getId());

        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }
}


