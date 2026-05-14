package com.example.envio.service;

import com.example.envio.model.Envio;
import com.example.envio.repository.EnvioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    public Envio createShipping(Long orderId) {

        log.info("Generando nuevo envío en base de datos para la orden n°: {}", orderId);

        Envio envio = new Envio();
        envio.setOrderId(orderId);
        envio.setStatus("PREPARING");
        envio.setTrackingNumber("TRK-" + System.currentTimeMillis());
        return envioRepository.save(envio);
    }

    public Envio getShippingByOrder(Long orderId) {
        return envioRepository.findByOrderId(orderId).orElse(null);
    }
}
