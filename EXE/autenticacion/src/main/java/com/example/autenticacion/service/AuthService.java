package com.example.autenticacion.service;

import com.example.autenticacion.dto.LoginRequest;
import com.example.autenticacion.dto.UsuarioResponse;
import com.example.autenticacion.security.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final WebClient.Builder webClientBuilder;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(WebClient.Builder webClientBuilder, JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.webClientBuilder = webClientBuilder;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public String procesarLogin(LoginRequest request) {
        log.info("Iniciando proceso de autenticación. Buscando usuario remoto...");
        UsuarioResponse usuarioEncontrado;

        try {
            // 1. LLAMADA AL VECINO
            usuarioEncontrado = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:9091/api/v1/usuarios/buscar/" + request.getEmail())
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block();

            log.info("Usuario remoto encontrado. Validando credenciales...");

        } catch (WebClientResponseException e) {
            log.warn("El microservicio de Usuarios rechazó la búsqueda: {}", e.getStatusCode());
            throw new RuntimeException("Credenciales incorrectas (Usuario no existe)");
        } catch (WebClientRequestException e) {
            log.error("No se pudo contactar al microservicio de Usuarios", e);
            throw new RuntimeException("El servicio de autenticación no está disponible en este momento");
        }

        // 2. CHOQUE DE CONTRASEÑAS
        if (!passwordEncoder.matches(request.getPassword(), usuarioEncontrado.getPassword())) {
            log.warn("Contraseña incorrecta ingresada para el correo: {}", request.getEmail());
            throw new RuntimeException("Credenciales incorrectas (Contraseña mala)");
        }

        // 3. FABRICAR EL TOKEN
        try {
            String nombreRol = usuarioEncontrado.getRoles().get(0).getNombre();
            log.info("Validacion exitosa. Generando token para rol: {}", nombreRol);
            return jwtProvider.generarToken(
                    usuarioEncontrado.getEmail(),
                    usuarioEncontrado.getId(),
                    nombreRol
            );
        } catch (IndexOutOfBoundsException e) {
            log.error("El usuario {} no tiene roles asignados en la base de datos", request.getEmail());
            throw new RuntimeException("Error interno: El usuario no tiene roles asignados");
        }
    }
}