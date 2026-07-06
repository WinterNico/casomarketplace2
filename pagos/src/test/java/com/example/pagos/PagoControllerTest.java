package com.example.pagos;

import com.example.pagos.controller.PagoController;
import com.example.pagos.dto.PagoRequest;
import com.example.pagos.dto.PagoResponse;
import com.example.pagos.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController pagoController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pagoController).build();
    }

    @Test
    void pagar_Aprobado() throws Exception {
        PagoRequest request = new PagoRequest();
        request.setIdPedido(1L);
        request.setMonto(1000.0);
        request.setNumeroTarjeta("1234567812345678");

        PagoResponse responseFalsa = new PagoResponse("TXN-123", "APROBADO", "Pago OK");

        when(pagoService.procesarPago(any(PagoRequest.class))).thenReturn(responseFalsa);

        mockMvc.perform(post("/api/v1/pagos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    @Test
    void pagar_Rechazado() throws Exception {
        PagoRequest request = new PagoRequest();
        request.setIdPedido(2L);
        request.setMonto(2000.0);
        request.setNumeroTarjeta("1111222233334444");

        PagoResponse responseFalsa = new PagoResponse("TXN-456", "RECHAZADO", "Bloqueada");

        when(pagoService.procesarPago(any(PagoRequest.class))).thenReturn(responseFalsa);

        mockMvc.perform(post("/api/v1/pagos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value("RECHAZADO"));
    }
}