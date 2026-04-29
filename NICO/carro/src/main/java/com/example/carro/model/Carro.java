package com.example.carro.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cart_items")
@Data
public class Carro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;

    private String productName;
    private Double unitPrice;

    public Double getTotalPrice() {
        if (this.unitPrice == null || this.quantity == null) {
            return 0.0;
        }

        return this.unitPrice * this.quantity;
    }
}