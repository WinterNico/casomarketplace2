package com.example.pagos.controller;

import com.example.pagos.dto.PagoRequest;
import com.example.pagos.dto.PagoResponse;
import com.example.pagos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/pagos")
@Tag(name = "Pagos", description = "Pasarela de simulación y procesamiento de pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/procesar")
    @Operation(
            summary = "Procesar un Pago",
            description = "Simula el cobro a una tarjeta. Si la tarjeta termina en 4444 el pago será rechazado, de lo contrario será aprobado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago procesado y aprobado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Pago rechazado (tarjeta terminada en 4444 o datos inválidos, otros.)")
    })
    public ResponseEntity<PagoResponse> pagar(@Valid @RequestBody PagoRequest request) {
        PagoResponse respuesta = pagoService.procesarPago(request);

        if (respuesta.getEstado().equals("RECHAZADO")) {
            return ResponseEntity.badRequest().body(respuesta);
        }
        return ResponseEntity.ok(respuesta);
    }
}