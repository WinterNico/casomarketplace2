package com.example.inventario.service;

import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    // --- TESTS PARA: checkStock ---

    @Test
    void deberiaRetornarTrueCuandoHayStockSuficiente() {
        Inventario inventario = new Inventario();
        inventario.setProductId(1L);
        inventario.setQuantity(10);

        when(inventarioRepository.findByProductId(1L)).thenReturn(Optional.of(inventario));

        boolean resultado = inventarioService.checkStock(1L, 5);

        assertTrue(resultado);
        verify(inventarioRepository).findByProductId(1L);
    }

    @Test
    void deberiaRetornarFalseCuandoNoHayStockSuficiente(){
        Inventario inventario = new Inventario();
        inventario.setProductId(1L);
        inventario.setQuantity(3);

        when(inventarioRepository.findByProductId(1L)).thenReturn(Optional.of(inventario));

        boolean resultado = inventarioService.checkStock(1L, 5);

        assertFalse(resultado);
        verify(inventarioRepository).findByProductId(1L);
    }

    @Test
    void deberiaRetornarFalseCuandoElProductoNoExiste() {
        // Simulamos que el repositorio no encontró el producto
        when(inventarioRepository.findByProductId(999L)).thenReturn(Optional.empty());

        boolean resultado = inventarioService.checkStock(999L, 1);

        assertFalse(resultado);
        verify(inventarioRepository).findByProductId(999L);
    }

    // --- TESTS PARA: addStock ---

    @Test
    void deberiaAgregarStockCorrectamente() {
        Inventario inventarioNuevo = new Inventario();
        inventarioNuevo.setProductId(100L);
        inventarioNuevo.setQuantity(50);

        Inventario inventarioGuardado = new Inventario();
        inventarioGuardado.setId(1L);
        inventarioGuardado.setProductId(100L);
        inventarioGuardado.setQuantity(50);

        when(inventarioRepository.save(inventarioNuevo)).thenReturn(inventarioGuardado);

        Inventario resultado = inventarioService.addStock(inventarioNuevo);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(100L, resultado.getProductId());
        assertEquals(50, resultado.getQuantity());
        verify(inventarioRepository).save(inventarioNuevo);
    }
}