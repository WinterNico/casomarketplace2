package com.example.pedidos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guardamos el ID del usuario que viene del microservicio de "usuarios"
    @Column(name = "usuario_id", nullable = false)
    private Long userId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime creationDate;

    // Podría ser un String o un Enum (ej: PENDIENTE, PAGADO, ENVIADO)
    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private BigDecimal total;

}
