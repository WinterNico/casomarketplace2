package com.example.usuarios.controller;

import com.example.usuarios.dto.RegistroRequest;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/buscar/{email}")
    public ResponseEntity<Usuario> obtenerUsuarioPorEmail(@PathVariable String email) {
        log.info("Petición REST para buscar usuario por email: {}", email);
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        log.info("Petición REST para buscar usuario por ID: {}", id);
        Usuario usuario = usuarioService.buscarPorId(id);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrarUsuario(@Valid @RequestBody RegistroRequest request) {
        log.info("Petición REST para registrar un nuevo usuario con email: {}", request.getEmail());

        // Ya no necesitamos el BindingResult ni los try-catch. Si algo falla, el @ControllerAdvice lo atrapa.
        Usuario nuevoUsuario = usuarioService.registrarUsuario(request);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }
}