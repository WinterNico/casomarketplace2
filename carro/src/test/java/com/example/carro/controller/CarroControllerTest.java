package com.example.carro.controller;

import com.example.carro.model.Carro;
import com.example.carro.security.JwtTokenFilter;
import com.example.carro.service.CarroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarroController.class)
@AutoConfigureMockMvc(addFilters = false)
class CarroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarroService cartService;

    @MockitoBean
    private JwtTokenFilter jwtTokenFilter;

    @Test
    void deberiaObtenerTotalDelCarro() throws Exception {
        when(cartService.getCartTotal(2L)).thenReturn(35000.0);

        mockMvc.perform(get("/api/v1/carro/2/total")
                        .header("Authorization", "Bearer token-de-mentira")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("35000.0"));

        verify(cartService).getCartTotal(2L);
    }

    @Test
    void deberiaEliminarItemDelCarro() throws Exception {
        doNothing().when(cartService).removeFromCart(10L);

        mockMvc.perform(delete("/api/v1/carro/remove/10")
                        .header("Authorization", "Bearer token-de-mentira")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Ítem eliminado"))
                .andExpect(jsonPath("$._links.volver-al-carrito.href").exists());

        verify(cartService).removeFromCart(10L);
    }
    @Test
    void deberiaObtenerCarroPorUsuario() throws Exception {
        Carro item = new Carro();
        item.setId(1L);
        item.setProductId(10L);
        item.setQuantity(2);
        item.setUnitPrice(15000.0);
        item.setProductName("Mouse Gamer");

        when(cartService.getCartByUserId(2L)).thenReturn(java.util.List.of(item));

        mockMvc.perform(get("/api/v1/carro/2")
                        .header("Authorization", "Bearer token-de-mentira")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(10))
                .andExpect(jsonPath("$[0].productName").value("Mouse Gamer"));

        verify(cartService).getCartByUserId(2L);
    }
}