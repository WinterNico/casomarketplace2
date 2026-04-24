package com.example.carro.service;

import com.example.carro.model.Carro;
import com.example.carro.repository.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class CarroService {
    @Autowired
    private CarroRepository carroRepository; // Esta es la variable que debes usar (con minúscula)

    @Autowired
    private RestTemplate restTemplate;

    private final String INVENTORY_URL = "http://localhost:8082/api/inventory";

    // Cambié el nombre a addToCart y Quantity a minúscula
    public Carro addToCart(Long userId, Long productId, Integer quantity) {
        String url = INVENTORY_URL + "/" + productId + "?quantityRequired=" + quantity;
        Boolean isStockAvailable = restTemplate.getForObject(url, Boolean.class);

        if (Boolean.FALSE.equals(isStockAvailable)) {
            throw new RuntimeException("No hay stock suficiente para el producto: " + productId);
        }

        // Corregido: usando carroRepository en minúscula
        Optional<Carro> existingItem = carroRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            Carro item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return carroRepository.save(item); // Corregido
        } else {
            Carro newItem = new Carro();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            return carroRepository.save(newItem); // Corregido
        }
    }

    public List<Carro> getCartByUserId(Long userId) {
        return carroRepository.findByUserId(userId);
    }

    public void removeFromCart(Long itemId) {
        carroRepository.deleteById(itemId);
    }
}