package com.example.usuarios.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.hateoas.RepresentationModel; // <-- IMPORTANTE
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "usuarios")
public class Usuario extends RepresentationModel<Usuario> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

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
        this.registrationDate = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }


}