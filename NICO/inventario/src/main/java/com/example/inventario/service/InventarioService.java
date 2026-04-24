package com.example.inventario.service;

import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class InventarioService {
    @Autowired
    private InventarioRepository inventarioRepository;

    // Método principal: Verifica si hay stock suficiente
    public boolean checkStock(Long productId, Integer quantityRequired) {
        Optional<Inventario> inventory = inventarioRepository.findByProductId(productId);

        // Si el producto existe en inventario Y la cantidad en bodega es mayor o igual a la requerida
        return inventory.isPresent() && inventory.get().getQuantity() >= quantityRequired;
    }

    // Método extra para que tú puedas agregar stock manual y hacer pruebas
    public Inventario addStock(Inventario inventory) {
        return inventarioRepository.save(inventory);
    }
}
