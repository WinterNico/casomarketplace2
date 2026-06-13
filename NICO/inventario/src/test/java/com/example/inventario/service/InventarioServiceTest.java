package com.example.inventario.service;

import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

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
        inventario.setQuantity(3); // Solo tenemos 3

        when(inventarioRepository.findByProductId(1L)).thenReturn(Optional.of(inventario));

        boolean resultado = inventarioService.checkStock(1L, 5);
        assertFalse(resultado);



    }
}
