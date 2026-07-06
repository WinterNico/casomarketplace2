package com.example.pedidos.service;

import com.example.pedidos.model.Pedido;
import com.example.pedidos.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
        boolean pagoRealizado = false; // Bandera para la compensación

        try {
            // 1. OBTENER EMAIL DEL USUARIO (Solución al hardcodeo)
            log.info("0. Obteniendo datos del usuario desde MS-Usuarios...");
            Map usuarioResponse = webClientBuilder.build().get()
                    .uri("http://usuarios/api/v1/usuarios/buscar/" + pedido.getUserId())
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String emailUsuario = usuarioResponse != null && usuarioResponse.containsKey("email")
                    ? (String) usuarioResponse.get("email")
                    : "correo_por_defecto@dominio.com";

            // 2. VALIDAR PAGO
            log.info("1. Validando pago con MS-Pagos...");
            Map<String, Object> pagoRequest = new HashMap<>();
            pagoRequest.put("idPedido", pedidoGuardado.getId());
            pagoRequest.put("numeroTarjeta", tarjeta);
            pagoRequest.put("monto", pedido.getTotal());

            webClientBuilder.build().post()
                    .uri("http://pagos/api/v1/pagos/procesar")
                    .header("Authorization", token)
                    .bodyValue(pagoRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Si pasa esta línea, el pago fue exitoso
            pagoRealizado = true;
            pedidoGuardado.setState("PAGADO");
            pedidoRepository.save(pedidoGuardado);

            // 3. REGISTRAR ENVÍO
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

            // 4. NOTIFICACIONES
            log.info("3. Envío creado. Disparando MS-Notificaciones...");
            Map<String, Object> notiRequest = new HashMap<>();
            notiRequest.put("emailDestino", emailUsuario); // Usamos el correo real del MS-Usuarios
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

            // EXCEPCIONES ESPECÍFICAS Y COMPENSACIÓN (SAGA)
        } catch (WebClientResponseException e) {
            log.error("Error de respuesta del servicio remoto: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            manejarCompensacion(pedidoGuardado, pagoRealizado, token);
            throw new RuntimeException("Fallo en la orquestación (Servicio remoto): " + e.getMessage());

        } catch (WebClientRequestException e) {
            log.error("Error de red/comunicación al contactar servicio remoto: {}", e.getMessage());
            manejarCompensacion(pedidoGuardado, pagoRealizado, token);
            throw new RuntimeException("Fallo en la orquestación (Error de red): " + e.getMessage());

        } catch (Exception e) {
            log.error("Error interno en la orquestación. Motivo: {}", e.getMessage());
            manejarCompensacion(pedidoGuardado, pagoRealizado, token);
            throw new RuntimeException("No se pudo completar la compra: " + e.getMessage());
        }

        return pedidoGuardado;
    }

    // MÉTODO DE COMPENSACIÓN (SAGA PATTERN)
    private void manejarCompensacion(Pedido pedido, boolean pagoRealizado, String token) {

        if (pagoRealizado) {
            log.warn("Iniciando compensación: Falló un proceso posterior. Reversando pago del pedido {}", pedido.getId());

            try {
                pedido.setState("REEMBOLSADO");
                pedidoRepository.save(pedido);   // <-- Ahora el save está dentro del try
                log.info("Compensación aplicada. El pedido quedó en estado REEMBOLSADO.");

            } catch (Exception ex) {
                log.error("Error CRÍTICO en compensación. Requiere revisión manual.");

                // Si falla guardar el reembolso, al menos dejamos el estado en memoria
                pedido.setState("ERROR_COMPENSACION");
            }

        } else {
            pedido.setState("RECHAZADO");
            pedidoRepository.save(pedido);
        }
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