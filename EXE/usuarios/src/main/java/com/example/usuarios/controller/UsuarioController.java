package com.example.usuarios.controller;

import com.example.usuarios.dto.RegistroRequest;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        log.info("Petición REST para buscar usuario por ID: {}", id);
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.add(linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel());
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrarUsuario(@Valid @RequestBody RegistroRequest request) {
        log.info("Petición REST para registrar un nuevo usuario con email: {}", request.getEmail());
        Usuario nuevoUsuario = usuarioService.registrarUsuario(request);
        nuevoUsuario.add(linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(nuevoUsuario.getId())).withSelfRel());
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }
}