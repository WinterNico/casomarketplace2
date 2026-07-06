package com.example.usuarios.controller;

import com.example.usuarios.dto.RegistroRequest;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.service.UsuarioService;
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

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void deberiaObtenerUsuarioPorId() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setName("Ren Amamiya");

        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/buscar/1")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ren Amamiya"))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(usuarioService).buscarPorId(1L);
    }

    @Test
    void deberiaObtenerUsuarioPorEmail() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("joker@phantom.cl");

        when(usuarioService.buscarPorEmail("joker@phantom.cl")).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/buscar/email/joker@phantom.cl")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joker@phantom.cl"))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(usuarioService).buscarPorEmail("joker@phantom.cl");
    }

    @Test
    void deberiaRegistrarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setName("Ren Amamiya");

        when(usuarioService.registrarUsuario(any(RegistroRequest.class))).thenReturn(usuario);

        String json = """
                {
                    "name": "Ren Amamiya",
                    "email": "joker@phantom.cl",
                    "password": "Password123!",
                    "nameRol": "ROLE_CLIENTE"
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios/registro")
                        .contentType("application/json")
                        .accept(MediaTypes.HAL_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ren Amamiya"))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(usuarioService).registrarUsuario(any(RegistroRequest.class));
    }
}