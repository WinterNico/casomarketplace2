package com.example.inventario.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InventarioTest {

    @Test
    void deberiaAsignarYObtenerValores() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductId(100L);
        inventario.setQuantity(50);

        assertEquals(1L, inventario.getId());
        assertEquals(100L, inventario.getProductId());
        assertEquals(50, inventario.getQuantity());
    }
}