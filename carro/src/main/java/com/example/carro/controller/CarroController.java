package com.example.carro.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.example.carro.DTO.AddCarroRequestDTO;
import com.example.carro.model.Carro;
import com.example.carro.service.CarroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carro")
@Slf4j
@Tag(name = "Gestor de carro", description = "Microservicio para gestionar el carro del marketplace")
public class CarroController {

    @Autowired
    private CarroService cartService;

    @PostMapping("/add")
    @Operation(summary = "Agrega productos al carro", description = "Añade un registro de los productos al carro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro se crea exitosamente"),
            @ApiResponse(responseCode = "400", description = "Fallo la validacion de uno de los campos"),
            @ApiResponse(responseCode = "403", description = "Se requiere autenticacion"),
            @ApiResponse(responseCode = "401", description = "Se requieren privilegios de administrador")
    })
    public ResponseEntity<Carro> addToCart(
            @Valid @RequestBody AddCarroRequestDTO request,
            @RequestHeader("Authorization") String token) {
        Carro item = cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity(), token);
        item.add(linkTo(methodOn(CarroController.class).getCart(item.getUserId())).withRel("ver-todos-del-usuario"));
        item.add(linkTo(methodOn(CarroController.class).getTotal(item.getUserId())).withRel("ver-total"));
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Obtener el carro de un usuario", description = "Devuelve la lista de todos los productos que un usuario específico tiene en su carro de compras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    public ResponseEntity<List<Carro>> getCart(@PathVariable Long userId) {
        List<Carro> listaCarros = cartService.getCartByUserId(userId);
        for (Carro carro : listaCarros) {
            carro.add(linkTo(methodOn(CarroController.class).getCart(userId)).withSelfRel());
            carro.add(linkTo(methodOn(CarroController.class).getTotal(userId)).withSelfRel());
        }
        return ResponseEntity.ok(listaCarros);
    }

    @DeleteMapping("/remove/{itemId}")
    @Operation(summary = "Eliminar un ítem del carro", description = "Elimina un registro específico del carro utilizando el ID del ítem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ítem eliminado correctamente del carro"),
            @ApiResponse(responseCode = "404", description = "El ítem no fue encontrado")
    })
    public ResponseEntity<EntityModel<java.util.Map<String, String>>> removeItem(@PathVariable Long itemId) {
        cartService.removeFromCart(itemId);
        java.util.Map<String, String> responseBody = java.util.Map.of("mensaje", "Ítem eliminado");
        return ResponseEntity.ok(EntityModel.of(responseBody,
                linkTo(methodOn(CarroController.class).getCart(null)).withRel("volver-al-carrito")));
    }

    @GetMapping("/{userId}/total")
    @Operation(summary = "Obtener el costo total del carro", description = "Calcula y retorna el valor total a pagar por todos los productos en el carro de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total calculado exitosamente")
    })
    public ResponseEntity<Double> getTotal(@PathVariable Long userId) {
        return new ResponseEntity<>(cartService.getCartTotal(userId), HttpStatus.OK);
    }
}