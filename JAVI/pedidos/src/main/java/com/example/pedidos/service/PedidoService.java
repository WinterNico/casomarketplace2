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

    //4. Obtener todos los pedidos de un usuario en particular
    public List<Pedido> getPedidosByUserId(Long userId){
        return pedidoRepository.findByUserId(userId);
    }

    //5. Obtener todos los pedidos que tengan un estado específico
    public List<Pedido> getPedidosByState(String state){
        return pedidoRepository.findByState(state);
    }

    //6. Actualizar el estado de un pedido (EJ: DE PENDIENTE A ENVIADO)
    public Pedido updateState(Long id, String newState){
        Optional<Pedido> existingPedido = pedidoRepository.findById(id);

        if(existingPedido.isPresent()){
            Pedido pedido = existingPedido.get();
            pedido.setState(newState);// Actualizamos al nuevo estado
            return pedidoRepository.save(pedido); // Guardamos los cambios
        }
        throw new RuntimeException("Pedido no encontrado con el id: " + id);
    }

    //7.Eliminar un Pedido
    public void deletePedido(Long id){
        pedidoRepository.deleteById(id);
    }
}
