package com.example.autenticacion.controller;

import com.example.autenticacion.dto.LoginRequest;
import com.example.autenticacion.dto.TokenResponse;
import com.example.autenticacion.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Autenticación", description = "Operaciones de inicio de sesión y generación de Tokens JWT")
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar Sesión",
            description = "Recibe el correo y contraseña. Se comunica con ms-usuarios para validar y devuelve un JWT de acceso."
    )
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Petición REST de login recibida para el correo: {}", request.getEmail());

        String token = authService.procesarLogin(request);
        log.info("Login exitoso. Token generado para: {}", request.getEmail());

        return new ResponseEntity<>(new TokenResponse(token), HttpStatus.OK);
    }
}