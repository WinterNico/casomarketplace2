package com.example.envio.controller;

import com.example.envio.dto.EnvioRequest;
import com.example.envio.service.EnvioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @PostMapping
    public String receivePedido(@Valid @RequestBody EnvioRequest request){
        // Imprime en la consola de Envíos para que veas que el dato llegó
        System.out.println(">>> Pedido recibido para procesar envío. Order ID: " + request.getId());

        // Llamamos al servicio para que se guarde en la DB
        envioService.createShipping(request.getId());


        return "Pedido recibido correctamente en el microservicio de envios";


    }
    @GetMapping("/order/{orderId}")
    public Object getShippingByOrder(@PathVariable Long orderId) {
        return envioService.getShippingByOrder(orderId);
    }
}
