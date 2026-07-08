package com.example.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "El ID del producto es obligatorio")
    @Positive(message = "El ID del producto debe ser un número válido")
    @Schema(name = "Id del producto", example = "100", description = "Identificador del producto asociado")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser menor a 0")
    @Schema(name = "Cantidad", example = "50", description = "Cantidad disponible en bodega")
    private Integer quantity;
}