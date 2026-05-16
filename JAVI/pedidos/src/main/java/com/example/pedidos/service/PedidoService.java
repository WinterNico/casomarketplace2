package com.example.pedidos.service;

import com.example.pedidos.model.Pedido;
import com.example.pedidos.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    // Crea el pedido y recibe el token
    public Pedido createPedido(Pedido pedido, String token){
        pedido.setCreationDate(LocalDateTime.now());
        pedido.setState("PENDIENTE");

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Preparamos el JSON exacto que espera el ms de envio
        Map<String, Long> envioRequest = new HashMap<>();
        envioRequest.put("id", pedidoGuardado.getId());

        // --- LLAMADA A ENVÍOS ---
        try {
            log.info("Avisando a ms-envios para generar el tracking...");
            webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8083/api/v1/envios")
                    .header("Authorization", token)
                    .bodyValue(envioRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            response -> log.info("Respuesta exitosa de Envíos: {}", response),
                            error -> log.error("Fallo al crear el envío remoto: {}", error.getMessage())
                    );
        } catch (Exception e) {
            log.error("Error crítico al contactar Envíos: {}", e.getMessage());
        }

        return pedidoGuardado;
    }

    // Obtener todos los pedidos
    public List<Pedido> getAllPedidos(){
        return pedidoRepository.findAll();
    }
    // Obtener un pedido específico por su ID
    public Optional<Pedido> getPedidoById(Long id){
        return pedidoRepository.findById(id);
    }

    // Obtener todos los pedidos de un usuario en particular
    public List<Pedido> getPedidosByUserId(Long userId){
        return pedidoRepository.findByUserId(userId);
    }

    // Obtener todos los pedidos que tengan un estado específico
    public List<Pedido> getPedidosByState(String state){
        return pedidoRepository.findByState(state);
    }

    // Actualizar el estado de un pedido (EJ: DE PENDIENTE A ENVIADO)
    public Pedido updateState(Long id, String newState){
        Optional<Pedido> existingPedido = pedidoRepository.findById(id);

        if(existingPedido.isPresent()){
            Pedido pedido = existingPedido.get();
            pedido.setState(newState);// Actualizamos al nuevo estado
            return pedidoRepository.save(pedido); // Guardamos los cambios
        }
        throw new RuntimeException("Pedido no encontrado con el id: " + id);
    }

    // Eliminar un Pedido
    public void deletePedido(Long id){
        pedidoRepository.deleteById(id);
    }
}
