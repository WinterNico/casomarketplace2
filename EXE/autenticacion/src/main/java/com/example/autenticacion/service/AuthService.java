package com.example.autenticacion.service;

import com.example.autenticacion.dto.LoginRequest;
import com.example.autenticacion.dto.UsuarioResponse;
import com.example.autenticacion.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class AuthService {

    private final WebClient.Builder webClientBuilder;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(WebClient.Builder webClientBuilder, JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.webClientBuilder = webClientBuilder;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public String procesarLogin(LoginRequest request) {
        UsuarioResponse usuarioEncontrado;

        try {
            // 1. LLAMADA AL VECINO: Vamos a tocarle la puerta al ms-usuarios
            // OJO: Asegúrate de que el puerto (9091) sea el correcto de tu ms-usuarios
            usuarioEncontrado = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:9091/api/v1/usuarios/buscar/" + request.getEmail())
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block(); // block() hace que el sistema espere la respuesta (síncrono)

        } catch (WebClientResponseException e) {
            // Si el ms-usuarios nos devuelve un 404 (Not Found), significa que el correo no existe
            throw new RuntimeException("Credenciales incorrectas (Usuario no existe)");
        }

        // 2. CHOQUE DE CONTRASEÑAS: Comparamos la que mandó el usuario con la encriptada
        if (!passwordEncoder.matches(request.getPassword(), usuarioEncontrado.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas (Contraseña mala)");
        }

        // 3. FABRICAR EL TOKEN: Si todo está bien, mandamos a hacer el pasaporte
        String nombreRol = usuarioEncontrado.getRoles().get(0).getNombre();
        return jwtProvider.generarToken(
                usuarioEncontrado.getEmail(),
                usuarioEncontrado.getId(),
                nombreRol // Pasamos el rol que acabamos de sacar
        );
    }
}