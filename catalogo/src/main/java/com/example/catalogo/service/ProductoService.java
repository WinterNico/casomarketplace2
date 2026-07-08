package com.example.catalogo.service;

import com.example.catalogo.model.Producto;
import com.example.catalogo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
    public void deleteProducto(Long id) {
        repository.deleteById(id);
    }

    public Producto updateProducto(Long id, Producto productoDetalles) {
        Producto p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        p.setNombre(productoDetalles.getNombre());
        p.setDescripcion(productoDetalles.getDescripcion());
        p.setPrecio(productoDetalles.getPrecio());

        return repository.save(p);
    }
}
