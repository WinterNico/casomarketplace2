package com.example.pedidos.controller;

import com.example.pedidos.model.Pedido;
import com.example.pedidos.repository.PedidoRepository;
import com.example.pedidos.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pedidos")// Esta es la URL base para este microservicio
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    //1. Crear un Pedido Nuevo
    // Se consume con un post a http://localhost:8080/api/pedidos
    @PostMapping
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido) {
        Pedido nuevoPedido = pedidoService.createPedido(pedido);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED); // rETORNA 201 CREATED

    }

    //2.Obtener un pedido por ID
    // Se consume con un GET a http://localhost:8080/api/pedidos/1
    @GetMapping
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }


    //3. Obtener un pedido por ID
    //Se consume con un get a http://localhost:8080/api/pedidos/1
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.getPedidoById(id);

        // Si el pedido existe, devuelve un 200 OK con el pedido. Si no devuelve 404 not Found.
        return pedido.map(value->new ResponseEntity<>(value, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    //4 Obtener pedidos por usuario
    // se consume con un GET a http://localhost:8080/api/pedidos/usuario/5
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Pedido>> getPedidosByUserId(@PathVariable Long userId) {
        List<Pedido> pedidos = pedidoService.getPedidosByUserId(userId);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }
    // 5. Obtener pedidos por estado
    // Se consume con un GET a http://localhost:8080/api/pedidos/estado/PENDING
    @GetMapping("/estado/{state}")
    public ResponseEntity<List<Pedido>> getPedidosByState(@PathVariable String state) {
        List<Pedido> pedidos = pedidoService.getPedidosByState(state);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }
    // 6. Actualizar el estado de un pedido
    // Se consume con un PUT a http://localhost:8080/api/pedidos/1/estado?newState=PAGADO
    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> updateState(@PathVariable Long id, @RequestParam String newstate) {
        try{
            Pedido pedidoActaulizado = pedidoService.updateState(id, newstate);
            return new ResponseEntity<>(pedidoActaulizado, HttpStatus.OK);
        }catch(RuntimeException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //7 Eliminar un pedido
    // Se consume con un DELETE a http://localhost:8080/api/pedidos/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id){
        pedidoService.deletePedido(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); //RETORNA 204 No content
    }
}
