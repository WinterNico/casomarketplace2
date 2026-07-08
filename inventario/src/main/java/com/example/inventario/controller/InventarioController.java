package com.example.inventario.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.example.inventario.model.Inventario;
import com.example.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Gestor de Inventario", description = "Microservicio para gestionar el stock de productos")
public class InventarioController {

    private static final Logger log = LoggerFactory.getLogger(InventarioController.class);

    @Autowired
    private InventarioService inventarioService;

    @GetMapping("/{productId}")
    @Operation(summary = "Consultar stock", description = "Verifica si hay stock suficiente para un producto")
    public ResponseEntity<EntityModel<Map<String, Boolean>>> checkStock(
            @PathVariable Long productId,
            @RequestParam Integer quantityRequired) {

        log.info("Consultando stock para Producto ID: {}, Cantidad requerida: {}", productId, quantityRequired);
        boolean isAvailable = inventarioService.checkStock(productId, quantityRequired);

        Map<String, Boolean> response = new HashMap<>();
        response.put("disponible", isAvailable);

        EntityModel<Map<String, Boolean>> model = EntityModel.of(response,
                linkTo(methodOn(InventarioController.class).checkStock(productId, quantityRequired)).withSelfRel());

        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @PostMapping("/add")
    @Operation(summary = "Agregar stock", description = "Ingresa nuevo stock a la bodega")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Stock agregado exitosamente") })
    public ResponseEntity<Inventario> addStock(@Valid @RequestBody Inventario inventory) {
        log.info("Ingresando stock a la bodega. Producto ID: {}, Cantidad: {}", inventory.getProductId(), inventory.getQuantity());

        Inventario saved = inventarioService.addStock(inventory);

        saved.add(linkTo(methodOn(InventarioController.class).checkStock(saved.getProductId(),1)).withRel("verificar-stock"));
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}