package com.example.pagos.service;

import com.example.pagos.dto.PagoRequest;
import com.example.pagos.dto.PagoResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PagoService {
    public PagoResponse procesarPago(PagoRequest request) {
        // 1. Simular la demora de comunicarse con Transbank/Visa (2 segundos)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Generar un código de transacción único aleatorio
        String txId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Lógica de simulacro: Si la tarjeta termina en "4444", simulamos un rechazo
        if (request.getNumeroTarjeta() != null && request.getNumeroTarjeta().endsWith("4444")) {
            return new PagoResponse(txId, "RECHAZADO", "Fondos insuficientes o tarjeta bloqueada");
        }

        // Si es cualquier otra tarjeta, pasa con éxito
        return new PagoResponse(txId, "APROBADO", "Pago procesado correctamente");
    }
}