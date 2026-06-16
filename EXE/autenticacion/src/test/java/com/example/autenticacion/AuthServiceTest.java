package com.example.autenticacion;

import com.example.autenticacion.dto.LoginRequest;
import com.example.autenticacion.dto.UsuarioResponse;
import com.example.autenticacion.security.JwtProvider;
import com.example.autenticacion.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // Actores
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    // la inyecion
    @InjectMocks
    private AuthService authService;

    // Variables de prueba
    private LoginRequest requestPrueba;
    private UsuarioResponse usuarioRemotoFalso;

    @BeforeEach
    void setUp() {
        // Datos
        requestPrueba = new LoginRequest();
        requestPrueba.setEmail("holaprueba@phantom.cl");
        requestPrueba.setPassword("PasswordSecreta123!");

        // El usuario falso que fingiremos que nos devuelve ms-usuarios
        UsuarioResponse.RolResponse rolFalso = new UsuarioResponse.RolResponse();
        rolFalso.setId(1L);
        rolFalso.setNombre("ROLE_CLIENTE");

        usuarioRemotoFalso = new UsuarioResponse();
        usuarioRemotoFalso.setId(1L);
        usuarioRemotoFalso.setEmail("holaprueba@phantom.cl");
        usuarioRemotoFalso.setPassword("hash_de_la_contraseña");
        usuarioRemotoFalso.setRoles(List.of(rolFalso));


        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    // Login exitoso
    @Test
    void procesarLogin_Exitoso() {
        // "Cuando el WebClient termine, devuelve el Mono con nuestro usuario falso"
        when(responseSpec.bodyToMono(UsuarioResponse.class)).thenReturn(Mono.just(usuarioRemotoFalso));

        // "Cuando comparen las contraseñas, di que SÍ coinciden (true)"
        when(passwordEncoder.matches("PasswordSecreta123!", "hash_de_la_contraseña")).thenReturn(true);

        // "Cuando pidan generar el token, devuelve este token inventado"
        when(jwtProvider.generarToken(anyString(), anyLong(), anyString())).thenReturn("eyJhbGciOiJIUzI1Ni...");

        // Ejecutamos
        String tokenGenerado = authService.procesarLogin(requestPrueba);

        // Comprobamos
        assertNotNull(tokenGenerado);
        assertEquals("eyJhbGciOiJIUzI1Ni...", tokenGenerado);

        // Verificamos que se compararon las contraseñas 1 vez
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    // Contraseña incorrecta
    @Test
    void procesarLogin_LanzaErrorPorContrasenaMala() {
        // El guion
        when(responseSpec.bodyToMono(UsuarioResponse.class)).thenReturn(Mono.just(usuarioRemotoFalso));

        // "Cuando comparen las contraseñas, di que NO coinciden (false)"
        when(passwordEncoder.matches("PasswordSecreta123!", "hash_de_la_contraseña")).thenReturn(false);

        // Esperamos la explosión controlada
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            authService.procesarLogin(requestPrueba);
        });

        assertEquals("Credenciales incorrectas (Contraseña mala)", excepcion.getMessage());

        // Comprobamos que, como falló la contraseña, NUNCA se intentó generar un token
        verify(jwtProvider, never()).generarToken(anyString(), anyLong(), anyString());
    }
}