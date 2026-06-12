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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarroController.class)
@AutoConfigureMockMvc(addFilters = false) // Apagamos la seguridad tal como lo hace el profe
class CarroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarroService cartService;

    @MockitoBean
    private JwtTokenFilter jwtTokenFilter;

    @Test
    void deberiaCrearRegistroEnElCarro() throws Exception {

        Carro carroResponse = new Carro();
        carroResponse.setId(1L);
        carroResponse.setUserId(2L);
        carroResponse.setProductId(10L);
        carroResponse.setQuantity(1);
        carroResponse.setProductName("Teclado Mecánico");
        carroResponse.setUnitPrice(45000.0);

        when(cartService.addToCart(anyLong(), anyLong(), anyInt(), anyString()))
                .thenReturn(carroResponse);

        String jsonRequest = """
                {
                    "userId": 2,
                    "productId": 10,
                    "quantity": 1
                }
                """;
        mockMvc.perform(post("/api/v1/carro/add")
                        .header("Authorization", "Bearer token-de-mentira") // Tu controller exige este header
                        .contentType("application/json")
                        .accept(MediaTypes.HAL_JSON) // Forzamos HATEOAS
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.productId").value(10))
                .andExpect(jsonPath("$.productName").value("Teclado Mecánico"))
                // Validamos que Spring HATEOAS generó los links
                .andExpect(jsonPath("$._links.ver-todos-del-usuario.href").exists())
                .andExpect(jsonPath("$._links.ver-total.href").exists());

        verify(cartService).addToCart(eq(2L), eq(10L), eq(1), anyString());
    }

    @Test
    void deberiaRetornar400CuandoFaltanCamposObligatorios() throws Exception {
        String jsonIncompleto = """
                {
                    "userId": 2
                }
                """;

        mockMvc.perform(post("/api/v1/carro/add")
                        .header("Authorization", "Bearer token-de-mentira")
                        .contentType("application/json")
                        .content(jsonIncompleto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de Validación"));
    }
}