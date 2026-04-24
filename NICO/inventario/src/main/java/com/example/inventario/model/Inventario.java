package com.example.inventario.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventory")
@Data
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El ID del producto que viene del Catálogo
    private Long productId;

    // La cantidad de stock disponible en bodega
    private Integer quantity;
}