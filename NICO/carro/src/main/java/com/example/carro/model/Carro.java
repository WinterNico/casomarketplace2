package com.example.carro.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Entity
@Table(name = "cart_items")
@Data
public class Carro extends RepresentationModel<Carro> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Id del carro", example = "1", description = "Carrito")
    private Long id;
    @Schema(name = "Id del usuario", example = "1", description = "Identificador del usuario")
    private Long userId;
    @Schema(name = "Id del producto", example = "1", description = "Identificador del producto")
    private Long productId;
    @Schema(name = "Cantidad", example = "3", description = "Numero de productos comprados")
    private Integer quantity;
    @Schema(name = "Nombre del producto", example = "Tarjeta Grafica", description = "El producto Deseado")
    private String productName;
    @Schema(name = "Precio unitario del producto", example = "10000", description = "Precio de cada uno de los productos del carro")
    private Double unitPrice;

    public Double getTotalPrice() {
        if (this.unitPrice == null || this.quantity == null) {
            return 0.0;
        }

        return this.unitPrice * this.quantity;
    }
}