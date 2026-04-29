package com.example.usuarios.controller;

import com.example.usuarios.dto.RegistroRequest;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Inyectamos el servicio
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {

        System.out.println("---- DATOS RECIBIDOS DESDE POSTMAN ----");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Password: " + request.getPassword());
        System.out.println("Rol: " + request.getNameRol());
        System.out.println("---------------------------------------");


        try {
            // Llamamos a la lógica de negocio que armamos antes
            Usuario nuevoUsuario = usuarioService.registrarUsuario(request);

            // Si todo sale bien, retornamos un 201 (Created) y los datos del usuario
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Si falla alguna validación (ej. el correo ya existe o el rol no es válido)
            // Retornamos un 400 (Bad Request) con el mensaje de error
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}