package com.example.pedidos.controller;

import com.example.pedidos.dto.PedidoRequestDTO;
import com.example.pedidos.model.Pedido;
import com.example.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos (Orquestador)", description = "Microservicio central que gestiona compras comunicándose con Pagos, Envíos y Notificaciones")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Crear Pedido", description = "Inicia el flujo orquestado de compra. Requiere el Token JWT del usuario.")
    public ResponseEntity<Pedido> createPedido(
            @Valid @RequestBody PedidoRequestDTO pedidoDTO,
            @RequestHeader("Authorization") String token) {

        log.info("Iniciando la creación de un pedido para el usuario con ID: {}", pedidoDTO.getUserId());

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUserId(pedidoDTO.getUserId());
        nuevoPedido.setTotal(pedidoDTO.getTotal());

        Pedido pedidoGuardado = pedidoService.createPedido(nuevoPedido, token, pedidoDTO.getTarjeta());

        log.info("Pedido creado exitosamente con un total de: {}", pedidoGuardado.getTotal());

        return new ResponseEntity<>(pedidoGuardado, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Devuelve los datos del pedido y sus enlaces HATEOAS")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        Optional<Pedido> pedidoOptional = pedidoService.getPedidoById(id);

        if (pedidoOptional.isPresent()) {
            Pedido pedido = pedidoOptional.get();
            // MAGIA HATEOAS
            pedido.add(linkTo(methodOn(PedidoController.class).getPedidoById(id)).withSelfRel());
            pedido.add(linkTo(methodOn(PedidoController.class).getPedidosByUserId(pedido.getUserId())).withRel("pedidos-del-usuario"));
            return new ResponseEntity<>(pedido, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Pedido>> getPedidosByUserId(@PathVariable Long userId) {
        List<Pedido> pedidos = pedidoService.getPedidosByUserId(userId);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @GetMapping("/estado/{state}")
    public ResponseEntity<List<Pedido>> getPedidosByState(@PathVariable String state) {
        List<Pedido> pedidos = pedidoService.getPedidosByState(state);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> updateState(@PathVariable Long id, @RequestParam String newstate) {
        try{
            Pedido pedidoActaulizado = pedidoService.updateState(id, newstate);
            return new ResponseEntity<>(pedidoActaulizado, HttpStatus.OK);
        }catch(RuntimeException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id){
        pedidoService.deletePedido(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}