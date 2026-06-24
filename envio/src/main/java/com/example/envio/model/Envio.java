package com.example.envio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "envios")
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor

public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String status;

    @Column(name = "tracking_number", nullable = false)
    private String trackingNumber;



}
