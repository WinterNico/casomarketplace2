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

    public boolean checkStock(Long productId, Integer quantityRequired) {
        Optional<Inventario> inventory = inventarioRepository.findByProductId(productId);
        return inventory.isPresent() && inventory.get().getQuantity() >= quantityRequired;
    }

    public Inventario addStock(Inventario inventory) {
        Optional<Inventario> existingInventory = inventarioRepository.findByProductId(inventory.getProductId());

        if (existingInventory.isPresent()) {
            Inventario inv = existingInventory.get();
            inv.setQuantity(inv.getQuantity() + inventory.getQuantity());
            return inventarioRepository.save(inv);
        } else {
            return inventarioRepository.save(inventory);
        }
    }
}