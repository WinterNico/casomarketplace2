package com.example.usuarios.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RolTest {
    @Test
    void deberiaCrearRolConConstructor() {
        Rol rol = new Rol("ROLE_ADMIN");
        assertEquals("ROLE_ADMIN", rol.getNombre());
    }
}