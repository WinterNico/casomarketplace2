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

    public Optional<Producto> getProductoById(Long id) {
        return repository.findById(id);
    }
    public Producto addProducto(Producto producto){
        return repository.save(producto);
    }
}
