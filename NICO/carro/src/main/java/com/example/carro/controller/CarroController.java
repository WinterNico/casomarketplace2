package com.example.carro.controller;

import com.example.carro.DTO.AddCarroRequestDTO;
import com.example.carro.model.Carro;
import com.example.carro.service.CarroService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carro") // <-- Agregamos el /v1/
@Slf4j
public class CarroController {

    @Autowired
    private CarroService cartService;

    @PostMapping("/add")
    public ResponseEntity<Carro> addToCart(
            @Valid @RequestBody AddCarroRequestDTO request,
            @RequestHeader("Authorization") String token) {

        log.info("Recibida la petición para agregar al carro. Usuario: {}, Producto: {}, Cantidad: {}",
                request.getUserId(), request.getProductId(), request.getQuantity());

        Carro item = cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity(), token);

        log.info("Producto {} agregado con éxito al carro del usuario {}", request.getProductId(), request.getUserId());
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Carro>> getCart(@PathVariable Long userId) {
        return new ResponseEntity<>(cartService.getCartByUserId(userId), HttpStatus.OK);
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long itemId) {
        cartService.removeFromCart(itemId);
        return new ResponseEntity<>(java.util.Map.of("mensaje", "Ítem eliminado del carrito"), HttpStatus.OK);
    }

    @GetMapping("/{userId}/total")
    public ResponseEntity<Double> getTotal(@PathVariable Long userId) {
        return new ResponseEntity<>(cartService.getCartTotal(userId), HttpStatus.OK);
    }
}