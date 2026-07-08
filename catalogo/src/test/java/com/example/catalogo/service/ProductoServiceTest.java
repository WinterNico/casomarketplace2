package com.example.catalogo.service;

import com.example.catalogo.model.Producto;
import com.example.catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    @Test
    void deberiaRetornarProductoCuandoExiste() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Mouse Gamer");

        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = service.getProductoById(1L);

        assertNotNull(resultado);
        assertEquals("Mouse Gamer", resultado.getNombre());
        verify(repository).findById(1L);
    }
    @Test
    void deberiaGuardarYRetornarProductoNuevo() {
        Producto producto = new Producto();
        producto.setNombre("Audífonos");

        Producto productoGuardado = new Producto();
        productoGuardado.setId(2L);
        productoGuardado.setNombre("Audífonos");

        when(repository.save(any(Producto.class))).thenReturn(productoGuardado);

        Producto resultado = service.addProducto(producto);

        assertNotNull(resultado.getId());
        assertEquals("Audífonos", resultado.getNombre());
        verify(repository).save(any(Producto.class));
    }
    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExiste() {
        Long idInexistente = 99L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            service.getProductoById(idInexistente);
        });
        verify(repository, times(1)).findById(idInexistente);
    }
    @Test
    void deberiaEliminarProductoCorrectamente() {
        Long id = 1L;
        doNothing().when(repository).deleteById(id);

        service.deleteProducto(id);

        verify(repository, times(1)).deleteById(id);
    }
    @Test
    void deberiaActualizarProductoExitosamente() {
        Long id = 1L;
        Producto existente = new Producto();
        existente.setId(id);
        existente.setNombre("Monitor Antiguo");

        Producto nuevosDatos = new Producto();
        nuevosDatos.setNombre("Monitor 4K");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Producto.class))).thenReturn(existente);

        Producto resultado = service.updateProducto(id, nuevosDatos);

        assertEquals("Monitor 4K", resultado.getNombre());
        verify(repository).save(any(Producto.class));
    }
}
