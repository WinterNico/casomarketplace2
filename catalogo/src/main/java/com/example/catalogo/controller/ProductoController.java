package com.example.catalogo.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.example.catalogo.model.Producto;
import com.example.catalogo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/catalogo")
@Tag(name = "Gestor de Catálogo", description = "Microservicio para administrar productos")
public class ProductoController {
    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService service;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Busca un producto especifico por su ID")
    public ResponseEntity<Producto> getProducto(@PathVariable Long id){
        log.info("Petición recibida para buscar producto ID: {}", id);

        // El controlador ya no valida el Optional. Delega la responsabilidad al servicio.
        Producto producto = service.getProductoById(id);

        producto.add(linkTo(methodOn(ProductoController.class).getProducto(id)).withSelfRel());
        log.info("Producto ID {} encontrado con éxito.", id);

        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @PostMapping("/add")
    @Operation(summary = "Agregar producto", description = "Registra un nuevo producto en el catálogo")
    public ResponseEntity<Producto> addProducto(@RequestBody Producto producto){
        log.info("Petición recibida para agregar nuevo producto: {}", producto.getNombre());
        Producto nuevoProducto = service.addProducto(producto);

        nuevoProducto.add(linkTo(methodOn(ProductoController.class).getProducto(nuevoProducto.getId())).withRel("ver-detalle"));
        log.info("Producto {} guardado con el ID: {}", nuevoProducto.getNombre(), nuevoProducto.getId());

        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }
}


