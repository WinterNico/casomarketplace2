package com.example.autenticacion.controller;

import com.example.autenticacion.dto.LoginRequest;
import com.example.autenticacion.dto.TokenResponse;
import com.example.autenticacion.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Le pedimos al servicio que procese el login y nos dé el token
            String token = authService.procesarLogin(request);

            // Lo empaquetamos bonito y devolvemos un 200 OK
            return new ResponseEntity<>(new TokenResponse(token), HttpStatus.OK);

        } catch (RuntimeException e) {
            // Si falla la contraseña o el correo, devolvemos un 401 Unauthorized
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}