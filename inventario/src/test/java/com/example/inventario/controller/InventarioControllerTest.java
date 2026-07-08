package com.example.inventario.controller;

import com.example.inventario.model.Inventario;
import com.example.inventario.security.JwtTokenFilter;
import com.example.inventario.service.InventarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventarioService inventarioService;

    @MockitoBean
    private JwtTokenFilter jwtTokenFilter;

    @Test
    void deberiaRetornarStockDisponibleConLinks() throws Exception {
        when(inventarioService.checkStock(100L, 2)).thenReturn(true);

        mockMvc.perform(get("/api/v1/inventory/100")
                        .param("quantityRequired", "2")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(true))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(inventarioService).checkStock(100L, 2);
    }

    @Test
    void deberiaAgregarStockYRetornarHateoas() throws Exception {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductId(100L);
        inventario.setQuantity(50);

        when(inventarioService.addStock(any(Inventario.class))).thenReturn(inventario);

        String json = """
                {
                    "productId": 100,
                    "quantity": 50
                }
                """;

        mockMvc.perform(post("/api/v1/inventory/add")
                        .contentType("application/json")
                        .accept(MediaTypes.HAL_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(100))
                .andExpect(jsonPath("$.quantity").value(50))
                .andExpect(jsonPath("$._links.verificar-stock.href").exists());

        verify(inventarioService).addStock(any(Inventario.class));
    }
}