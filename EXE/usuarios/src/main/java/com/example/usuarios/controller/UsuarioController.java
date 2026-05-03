package com.example.usuarios.controller;

import com.example.usuarios.dto.RegistroRequest;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET //
    @GetMapping("/buscar/{email}")
    public ResponseEntity<?> obtenerUsuarioPorEmail(@PathVariable String email) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(email);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // POST //
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroRequest request, BindingResult result) {










        if (result.hasErrors()) {
            // Extraemos el mensaje de error que escribiste en el DTO y devolvemos un 400 limpiecito
            String mensajeError = result.getFieldError().getDefaultMessage();
            return new ResponseEntity<>(mensajeError, HttpStatus.BAD_REQUEST);
        }

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