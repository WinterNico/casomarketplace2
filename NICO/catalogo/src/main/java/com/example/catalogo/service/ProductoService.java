package com.example.catalogo.service;

import com.example.catalogo.model.Producto;
import com.example.catalogo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository repository;

    public Producto crearProducto(Producto producto) {
        if (producto.getPrecio() <= 0){
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        return repository.save(producto);
    }
    public List<Producto> obtenerTodos(){
        return repository.findAll();
    }
}
