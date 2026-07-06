package com.example.catalogo.service;

import com.example.catalogo.model.Producto;
import com.example.catalogo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository repository;

    public Producto getProductoById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
    public Producto addProducto(Producto producto){
        return repository.save(producto);
    }
    // Asegúrate de tener estos métodos en tu ProductoService.java
    public void deleteProducto(Long id) {
        repository.deleteById(id);
    }

    public Producto updateProducto(Long id, Producto productoDetalles) {
        Producto p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Actualizamos los campos que sí tienes en tu modelo
        p.setNombre(productoDetalles.getNombre());
        p.setDescripcion(productoDetalles.getDescripcion());
        p.setPrecio(productoDetalles.getPrecio());

        return repository.save(p);
    }
}
