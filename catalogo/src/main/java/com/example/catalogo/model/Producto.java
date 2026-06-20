package com.example.catalogo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
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
    @Schema(name = "Nombre", example = "Monitor 144Hz", description = "Nombre comercial del producto")
    private String nombre;
    @Schema(name = "Descripción", example = "Monitor gamer curvo", description = "Detalle del producto")
    private String descripcion;
    @Schema(name = "Precio", example = "150000.0", description = "Precio unitario del producto")
    private Double precio;
}