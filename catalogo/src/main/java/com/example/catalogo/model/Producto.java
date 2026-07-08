package com.example.catalogo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Entity
@Table(name = "productos")
@Data
public class Producto extends RepresentationModel<Producto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Id", example = "1", description = "Identificador único del producto")
    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Schema(name = "Nombre", example = "Monitor 144Hz", description = "Nombre comercial del producto")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Schema(name = "Descripción", example = "Monitor gamer curvo", description = "Detalle del producto")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un número mayor a 0")
    @Schema(name = "Precio", example = "150000.0", description = "Precio unitario del producto")
    private Double precio;
}