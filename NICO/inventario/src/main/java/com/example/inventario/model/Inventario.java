package com.example.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Entity
@Table(name = "inventory")
@Data
public class Inventario extends RepresentationModel<Inventario> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Id", example = "1", description = "Identificador del registro de inventario")
    private Long id;
    @Schema(name = "Id del producto", example = "100", description = "Identificador del producto asociado")
    private Long productId;
    @Schema(name = "Cantidad", example = "50", description = "Cantidad disponible en bodega")
    private Integer quantity;
}