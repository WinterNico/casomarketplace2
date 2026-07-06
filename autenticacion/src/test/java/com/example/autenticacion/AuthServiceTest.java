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
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

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

    @InjectMocks
    private AuthService authService;

    private LoginRequest requestPrueba;
    private UsuarioResponse usuarioRemotoFalso;

    @BeforeEach
    void setUp() {
        requestPrueba = new LoginRequest();
        requestPrueba.setEmail("holaprueba@phantom.cl");
        requestPrueba.setPassword("PasswordSecreta123!");

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

    @Test
    void procesarLogin_Exitoso() {
        when(responseSpec.bodyToMono(UsuarioResponse.class)).thenReturn(Mono.just(usuarioRemotoFalso));

        when(passwordEncoder.matches("PasswordSecreta123!", "hash_de_la_contraseña")).thenReturn(true);

        when(jwtProvider.generarToken(anyString(), anyLong(), anyString())).thenReturn("eyJhbGciOiJIUzI1Ni...");

        String tokenGenerado = authService.procesarLogin(requestPrueba);

        assertNotNull(tokenGenerado);
        assertEquals("eyJhbGciOiJIUzI1Ni...", tokenGenerado);

        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    void procesarLogin_LanzaErrorPorContrasenaMala() {
        when(responseSpec.bodyToMono(UsuarioResponse.class)).thenReturn(Mono.just(usuarioRemotoFalso));

        when(passwordEncoder.matches("PasswordSecreta123!", "hash_de_la_contraseña")).thenReturn(false);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            authService.procesarLogin(requestPrueba);
        });

        assertEquals("Credenciales incorrectas (Contraseña mala)", excepcion.getMessage());

        verify(jwtProvider, never()).generarToken(anyString(), anyLong(), anyString());
    }

    @Test
    void procesarLogin_LanzaErrorPorWebClientResponse() {
        WebClientResponseException errorHttp = WebClientResponseException.create(404, "Not Found", null, null, null);
        when(responseSpec.bodyToMono(UsuarioResponse.class)).thenThrow(errorHttp);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            authService.procesarLogin(requestPrueba);
        });

        assertEquals("Credenciales incorrectas (Usuario no existe)", excepcion.getMessage());
    }

    @Test
    void procesarLogin_LanzaErrorPorWebClientRequest() {
        WebClientRequestException errorRed = new WebClientRequestException(
                new RuntimeException("Timeout"),
                HttpMethod.GET,
                URI.create("http://localhost"),
                HttpHeaders.EMPTY
        );

        when(responseSpec.bodyToMono(UsuarioResponse.class)).thenThrow(errorRed);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            authService.procesarLogin(requestPrueba);
        });

        assertEquals("El servicio de autenticación no está disponible en este momento", excepcion.getMessage());
    }



    @Test
    void procesarLogin_LanzaErrorPorUsuarioSinRoles() {
        usuarioRemotoFalso.setRoles(List.of());

        when(responseSpec.bodyToMono(UsuarioResponse.class)).thenReturn(Mono.just(usuarioRemotoFalso));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            authService.procesarLogin(requestPrueba);
        });

        assertEquals("Error interno: El usuario no tiene roles asignados", excepcion.getMessage());
    }
}