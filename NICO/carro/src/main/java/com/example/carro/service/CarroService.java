package com.example.carro.service;

import com.example.carro.DTO.ProductoDTO;
import com.example.carro.client.CatalogoClient;
import com.example.carro.client.InventarioClient;
import com.example.carro.model.Carro;
import com.example.carro.repository.CarroRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CarroService {

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private CatalogoClient catalogoClient;

    @Autowired
    private InventarioClient inventarioClient;

    public Carro addToCart(Long userId, Long productId, Integer quantity) {
        log.info("Iniciando validación de producto {} en Catálogo...", productId);
        ProductoDTO producto;
        try {
            producto = catalogoClient.getProducto(productId);
            log.info("Producto encontrado en catálogo: {}", producto.getNombre());
        } catch (Exception e) {
            log.error("Fallo al contactar al Catálogo para el producto ID: {}", productId);
            throw new RuntimeException("El producto no existe en el catálogo.");
        }
        log.info("Verificando stock para {} unidades del producto {}...", quantity, productId);
        Boolean isStockAvailable = inventarioClient.checkStock(productId, quantity);

        if (Boolean.FALSE.equals(isStockAvailable)) {
            log.warn("Stock insuficiente para el producto ID: {}. Solicitado: {}", productId, quantity);
            throw new RuntimeException("No hay stock suficiente para el producto: " + productId);
        }

        Optional<Carro> existingItem = carroRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            Carro item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setProductName(producto.getNombre());
            item.setUnitPrice(producto.getPrecio());
            log.info("Actualizando ítem en el carro. Usuario: {}, Producto: {}, Nueva cantidad: {}", userId, productId, item.getQuantity());
            return carroRepository.save(item);
        } else {
            Carro newItem = new Carro();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setProductName(producto.getNombre());
            newItem.setUnitPrice(producto.getPrecio());
            log.info("Agregando nuevo producto al carro. Usuario: {}, Producto: {}, Cantidad: {}", userId, productId, quantity);
            return carroRepository.save(newItem);
        }
    }

    public Double getCartTotal(Long userId) {
        log.info("Calculando el precio total del carro para el usuario ID: {}", userId);
        List<Carro> items = carroRepository.findByUserId(userId);
        Double total = items.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
        log.info("Total calculado para el usuario {}: ${}", userId, total);
        return total;
    }

    public List<Carro> getCartByUserId(Long userId) {
        log.info("Obteniendo lista de items del carro para el usuario ID: {}", userId);
        return carroRepository.findByUserId(userId);
    }

    public void removeFromCart(Long itemId) {
        if (!carroRepository.existsById(itemId)) {
            log.warn("Intento de eliminación fallido. No se encontró el ítem ID {} en el carro.", itemId);
            throw new RuntimeException("El ítem a eliminar no existe.");
        }
        log.info("Eliminando el ítem ID {} del carro", itemId);
        carroRepository.deleteById(itemId);
    }
}