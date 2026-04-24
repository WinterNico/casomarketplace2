package com.example.pedidos.service;

import com.example.pedidos.model.Pedido;
import com.example.pedidos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    // 1. Crear un nuevo pedido (Aplica reglas de negocio)
    public Pedido createPedido(Pedido pedido){
        pedido.setCreationDate(LocalDateTime.now());//Cuando nace un pedido, se le asigna la fecha actual y estado PENDIENTE
        pedido.setState("PENDIENTE");

        return pedidoRepository.save(pedido);
    }

    // 2. Obtener todos los pedidos
    public List<Pedido> getAllPedidos(){
        return pedidoRepository.findAll();
    }
    // 3. Obtener un pedido específico por su ID
    public Optional<Pedido> getPedidoById(Long id){
        return pedidoRepository.findById(id);
    }
}
