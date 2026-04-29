package com.example.carro.service;

import com.example.carro.DTO.ProductoDTO;
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
    private CarroRepository carroRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String INVENTORY_URL = "http://localhost:8082/api/inventory";

    private final String CATALOGO_URL = "http://localhost:8081/api/catalogo";

    public Carro addToCart(Long userId, Long productId, Integer quantity) {
        String catalogoUrl = CATALOGO_URL + "/" + productId;
        ProductoDTO producto;
        try {
            producto = restTemplate.getForObject(catalogoUrl, ProductoDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("El producto no existe en el catálogo.");
        }

        String url = INVENTORY_URL + "/" + productId + "?quantityRequired=" + quantity;
        Boolean isStockAvailable = restTemplate.getForObject(url, Boolean.class);

        if (Boolean.FALSE.equals(isStockAvailable)) {
            throw new RuntimeException("No hay stock suficiente para el producto: " + productId);
        }
        Optional<Carro> existingItem = carroRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            Carro item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setProductName(producto.getNombre());
            item.setUnitPrice(producto.getPrecio());
            return carroRepository.save(item);
        } else {
            Carro newItem = new Carro();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setProductName(producto.getNombre());
            newItem.setUnitPrice(producto.getPrecio());
            return carroRepository.save(newItem);
        }
    }

    public Double getCartTotal(Long userId){
        List<Carro> items = carroRepository.findByUserId(userId);
        return items.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
    }

    public List<Carro> getCartByUserId(Long userId) {
        return carroRepository.findByUserId(userId);
    }

    public void removeFromCart(Long itemId) {
        carroRepository.deleteById(itemId);
    }
}