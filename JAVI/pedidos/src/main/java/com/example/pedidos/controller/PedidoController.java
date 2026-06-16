    package com.example.pedidos.controller;

    import com.example.pedidos.dto.PedidoRequestDTO;
    import com.example.pedidos.model.Pedido;
    import com.example.pedidos.repository.PedidoRepository;
    import com.example.pedidos.service.PedidoService;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

    import java.util.List;
    import java.util.Optional;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    @RestController
    @RequestMapping("/api/v1/pedidos")// Esta es la URL base para este microservicio
    public class PedidoController {

        private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

        @Autowired
        private PedidoService pedidoService;

        // Crear un Pedido Nuevo
        @PostMapping
        public ResponseEntity<Pedido> createPedido(
                @Valid @RequestBody PedidoRequestDTO pedidoDTO,
                @RequestHeader("Authorization") String token) { // aca va el token

            log.info("Iniciando la creación de un pedido para el usuario con ID: {}", pedidoDTO.getUserId());

            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setUserId(pedidoDTO.getUserId());
            nuevoPedido.setTotal(pedidoDTO.getTotal());

            // Le pasamos el token al servicio para que se lo envíe a Envíos
            Pedido pedidoGuardado = pedidoService.createPedido(nuevoPedido, token, pedidoDTO.getTarjeta());

            log.info("Pedido creado exitosamente con un total de: {}", pedidoGuardado.getTotal());

            return new ResponseEntity<>(pedidoGuardado, HttpStatus.CREATED);
        }

    @GetMapping
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    // Obtener un pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.getPedidoById(id);

        // Si el pedido existe, devuelve un 200 OK con el pedido. Si no devuelve 404 not Found.
        return pedido.map(value->new ResponseEntity<>(value, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Obtener por ID de usuario
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Pedido>> getPedidosByUserId(@PathVariable Long userId) {
        List<Pedido> pedidos = pedidoService.getPedidosByUserId(userId);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    // Obtener pedidos por estado de estos
    @GetMapping("/estado/{state}")
    public ResponseEntity<List<Pedido>> getPedidosByState(@PathVariable String state) {
        List<Pedido> pedidos = pedidoService.getPedidosByState(state);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    // Actualiza el estado de un pedido
    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> updateState(@PathVariable Long id, @RequestParam String newstate) {
        try{
            Pedido pedidoActaulizado = pedidoService.updateState(id, newstate);
            return new ResponseEntity<>(pedidoActaulizado, HttpStatus.OK);
        }catch(RuntimeException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Elimina un pedido por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id){
        pedidoService.deletePedido(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); //RETORNA 204 No content
    }
}
