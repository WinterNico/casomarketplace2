package com.example.pedidos.repository;

import com.example.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {


    List<Pedido> findByUserId(Long userId);


    List<Pedido> findByState(String state);


    List<Pedido> findByUserIdAndState(Long userId, String state);
}
