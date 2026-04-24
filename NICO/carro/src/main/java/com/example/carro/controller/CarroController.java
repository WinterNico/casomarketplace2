package com.example.carro.controller;

import com.example.carro.model.Carro;
import com.example.carro.service.CarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carro")
public class CarroController {

    @Autowired
    private CarroService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long userId,
                                       @RequestParam Long productId,
                                       @RequestParam Integer quantity) {
        try {
            Carro item = cartService.addToCart(userId, productId, quantity);
            return new ResponseEntity<>(item, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Carro>> getCart(@PathVariable Long userId) {
        return new ResponseEntity<>(cartService.getCartByUserId(userId), HttpStatus.OK);
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> removeItem(@PathVariable Long itemId) {
        cartService.removeFromCart(itemId);
        return new ResponseEntity<>("Ítem eliminado del carrito", HttpStatus.OK);
    }
}