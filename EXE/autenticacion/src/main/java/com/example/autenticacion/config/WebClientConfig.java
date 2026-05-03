package com.example.autenticacion.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    // Este Bean es como darle un teléfono celular al microservicio
    // para que pueda llamar a la API de Usuarios cuando lo necesite.
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}
