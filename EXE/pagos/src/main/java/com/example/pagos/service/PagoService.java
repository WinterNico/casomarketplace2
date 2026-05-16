package com.example.pagos.service;

import com.example.pagos.dto.PagoRequest;
import com.example.pagos.dto.PagoResponse;
import com.example.pagos.model.Pago;
import com.example.pagos.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);
    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public PagoResponse procesarPago(PagoRequest request) {
        log.info("Iniciando procesamiento de pago para el pedido ID: {}", request.getIdPedido());

        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String txId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String estado = "APROBADO";
        String mensaje = "Pago procesado correctamente";

        // Si la tarjeta termina en 444 dara error
        if (request.getNumeroTarjeta() != null && request.getNumeroTarjeta().endsWith("4444")) {
            estado = "RECHAZADO";
            mensaje = "Fondos insuficientes o tarjeta bloqueada";
            log.warn("Pago rechazado para el pedido ID: {}. Razón: {}", request.getIdPedido(), mensaje);
        } else {
            log.info("Pago aprobado con éxito. Transacción: {}", txId);
        }

        // Guardamos el registro en la BD
        Pago registro = new Pago();
        registro.setIdPedido(request.getIdPedido());
        registro.setMonto(request.getMonto());
        registro.setTransaccionId(txId);
        registro.setEstado(estado);
        pagoRepository.save(registro);

        return new PagoResponse(txId, estado, mensaje);
    }
}