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
    public Pedido createPedido(Pedido pedido, String token, String tarjeta){
        pedido.setCreationDate(LocalDateTime.now());
        pedido.setState("PROCESANDO"); // Estado inicial

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        try {
            log.info("1. Validando pago con MS-Pagos...");
            // Armamos el JSON que espera Pagos
            Map<String, Object> pagoRequest = new HashMap<>();
            pagoRequest.put("idPedido", pedidoGuardado.getId());
            pagoRequest.put("numeroTarjeta", tarjeta);
            pagoRequest.put("monto", pedido.getTotal());

            // Llamamos a Pagos (usamos .block() para que espere la respuesta)
            webClientBuilder.build().post()
                    .uri("http://pagos/api/v1/pagos/procesar")
                    .header("Authorization", token)
                    .bodyValue(pagoRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Si llegamos a esta línea, es porque Pagos NO tiró error (tarjeta válida)
            pedidoGuardado.setState("PAGADO");
            pedidoRepository.save(pedidoGuardado);

            log.info("2. Pago exitoso. Avisando a MS-Envios...");
            Map<String, Long> envioRequest = new HashMap<>();
            envioRequest.put("id", pedidoGuardado.getId());

            webClientBuilder.build().post()
                    .uri("http://envios/api/v1/envios")
                    .header("Authorization", token)
                    .bodyValue(envioRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Map<String, Object> notiRequest = new HashMap<>();

            log.info("3. Envío creado. Disparando MS-Notificaciones...");
            notiRequest.put("emailDestino", "cliente_" + pedidoGuardado.getUserId() + "@duoc.cl");
            notiRequest.put("asunto", "Confirmación de Pedido #" + pedidoGuardado.getId());
            notiRequest.put("mensaje", "¡Tu compra por $" + pedido.getTotal() + " fue aprobada y se está preparando!");

            webClientBuilder.build().post()
                    .uri("http://notificaciones/api/v1/notificaciones/enviar")
                    .header("Authorization", token)
                    .bodyValue(notiRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("¡FLUJO COMPLETO EXITOSO!");

        } catch (Exception e) {
            log.error("Error en la orquestación. Motivo: {}", e.getMessage());
            // Si cualquier cosa falla, pagos, lo marcamos como rechazado
            pedidoGuardado.setState("RECHAZADO");
            pedidoRepository.save(pedidoGuardado);

            // Lanzamos el error para que tu GlobalExceptionHandler lo atrape y se lo muestre a Postman
            throw new RuntimeException("No se pudo completar la compra: " + e.getMessage());
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
