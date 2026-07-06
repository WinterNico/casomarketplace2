package com.example.catalogo.controller;

import com.example.catalogo.model.Producto;
import com.example.catalogo.security.JwtTokenFilter;
import com.example.catalogo.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService service;

    @MockitoBean
    private JwtTokenFilter jwtTokenFilter;

    @Test
    void deberiaRetornarProductoPorIdConLinks() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Silla Ergonomica");
        producto.setPrecio(120000.0);

        when(service.getProductoById(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/v1/catalogo/1")
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Silla Ergonomica"))
                .andExpect(jsonPath("$._links.self.href").exists());

            verify(service).getProductoById(1L);



    }

    @Test
    void deberiaCrearProductoYRetornarHateoas() throws Exception {
        Producto producto = new Producto();
        producto.setId(2L);
        producto.setNombre("Monitor 24");
        producto.setPrecio(150000.0);

        when(service.addProducto(any(Producto.class))).thenReturn(producto);

        String json = """
                {
                    "nombre": "Monitor 24",
                    "descripcion": "Resolución 1080p",
                    "precio": 150000.0
                }
                """;

        mockMvc.perform(post("/api/v1/catalogo/add")
                        .contentType("application/json")
                        .accept(MediaTypes.HAL_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nombre").value("Monitor 24"))
                .andExpect(jsonPath("$._links.ver-detalle.href").exists());

        verify(service).addProducto(any(Producto.class));
    }
}
