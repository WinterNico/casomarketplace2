package com.example.usuarios.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {
    @Test
    void deberiaEjecutarPrePersistYAgregarRol() {
        Usuario usuario = new Usuario();
        usuario.setActivo(null);
        usuario.prePersist();

        assertNotNull(usuario.getRegistrationDate());
        assertTrue(usuario.getActivo());

        Rol rol = new Rol("ROLE_ADMIN");
        usuario.agregarRol(rol);
        assertTrue(usuario.getRoles().contains(rol));
    }
}