package com.example.pedidos;

import com.example.pedidos.controller.PedidoController;
import com.example.pedidos.dto.PedidoRequestDTO;
import com.example.pedidos.model.Pedido;
import com.example.pedidos.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pedido crearPedido() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setUserId(99L);
        p.setTotal(new BigDecimal("150000"));
        p.setState("PAGADO");
        return p;
    }

    @Test
    void createPedido() throws Exception {

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setUserId(99L);
        dto.setTarjeta("1234567812345678");
        dto.setTotal(new BigDecimal("150000"));

        when(pedidoService.createPedido(any(), anyString(), anyString()))
                .thenReturn(crearPedido());

        mockMvc.perform(post("/api/v1/pedidos")
                        .header("Authorization","Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllPedidos() throws Exception {

        when(pedidoService.getAllPedidos())
                .thenReturn(List.of(crearPedido()));

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    void getPedidoByIdExiste() throws Exception {

        when(pedidoService.getPedidoById(1L))
                .thenReturn(Optional.of(crearPedido()));

        mockMvc.perform(get("/api/v1/pedidos/1")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPedidoByIdNoExiste() throws Exception {

        when(pedidoService.getPedidoById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPedidosByUsuario() throws Exception {

        when(pedidoService.getPedidosByUserId(99L))
                .thenReturn(List.of(crearPedido()));

        mockMvc.perform(get("/api/v1/pedidos/usuario/99"))
                .andExpect(status().isOk());
    }

    @Test
    void getPedidosByEstado() throws Exception {

        when(pedidoService.getPedidosByState("PAGADO"))
                .thenReturn(List.of(crearPedido()));

        mockMvc.perform(get("/api/v1/pedidos/estado/PAGADO"))
                .andExpect(status().isOk());
    }

    @Test
    void updateState() throws Exception {

        Pedido pedido = crearPedido();
        pedido.setState("ENVIADO");

        when(pedidoService.updateState(1L,"ENVIADO"))
                .thenReturn(pedido);

        mockMvc.perform(put("/api/v1/pedidos/1/estado")
                        .param("newstate","ENVIADO"))
                .andExpect(status().isOk());
    }

    @Test
    void deletePedido() throws Exception {

        doNothing().when(pedidoService).deletePedido(1L);

        mockMvc.perform(delete("/api/v1/pedidos/1"))
                .andExpect(status().isNoContent());
    }
}