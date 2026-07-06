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
        // GIVEN: Preparamos los datos y el comportamiento simulado
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Mouse Gamer");

        // La configuración del mock pertenece a la fase de preparación (GIVEN)
        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        // WHEN: Ejecutamos el método del servicio (ahora devuelve Producto, no Optional)
        Producto resultado = service.getProductoById(1L);

        // THEN: Validamos que el resultado sea el que esperamos
        assertNotNull(resultado); // Reemplaza al isPresent()
        assertEquals("Mouse Gamer", resultado.getNombre()); // Ya no se necesita el .get()
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
        // GIVEN: ID inexistente
        Long idInexistente = 99L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        // WHEN & THEN: Verificamos que se lance la excepción correcta
        assertThrows(RuntimeException.class, () -> {
            service.getProductoById(idInexistente);
        });
        verify(repository, times(1)).findById(idInexistente);
    }
    @Test
    void deberiaEliminarProductoCorrectamente() {
        // GIVEN
        Long id = 1L;
        doNothing().when(repository).deleteById(id);

        // WHEN
        service.deleteProducto(id);

        // THEN
        verify(repository, times(1)).deleteById(id);
    }
    @Test
    void deberiaActualizarProductoExitosamente() {
        // GIVEN
        Long id = 1L;
        Producto existente = new Producto();
        existente.setId(id);
        existente.setNombre("Monitor Antiguo");

        Producto nuevosDatos = new Producto();
        nuevosDatos.setNombre("Monitor 4K");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Producto.class))).thenReturn(existente);

        // WHEN
        Producto resultado = service.updateProducto(id, nuevosDatos);

        // THEN
        assertEquals("Monitor 4K", resultado.getNombre());
        verify(repository).save(any(Producto.class));
    }
}
