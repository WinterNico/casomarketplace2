package com.example.pagos.controller;

import com.example.pagos.dto.PagoRequest;
import com.example.pagos.dto.PagoResponse;
import com.example.pagos.service.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/procesar")
    public ResponseEntity<PagoResponse> pagar(@RequestBody PagoRequest request) {
        PagoResponse respuesta = pagoService.procesarPago(request);

        if (respuesta.getEstado().equals("RECHAZADO")) {
            return ResponseEntity.badRequest().body(respuesta); // Status 400
        }

        return ResponseEntity.ok(respuesta); // Status 200
    }
}