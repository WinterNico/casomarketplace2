package com.example.usuarios.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    // ESTO SIRVE PARA RELACION CON ROLES //
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();


    // Metodo utilitario para agregar roles
    public void agregarRol(Rol rol) {
        this.roles.add(rol);
    }

    // Se ejecuta ANTES de insertar en la BD
    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }

}