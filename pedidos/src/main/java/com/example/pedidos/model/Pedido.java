package com.example.pedidos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "pedidos")
public class Pedido extends RepresentationModel<Pedido> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long userId;

    @Column(name = "fecha-creacion", nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private BigDecimal total;

}
