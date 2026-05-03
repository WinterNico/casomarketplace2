package com.example.autenticacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Clásico para APIs REST
                .authorizeHttpRequests(auth -> auth
                        // ¡Vía libre absoluta a la ruta de login y a los errores!
                        .requestMatchers("/api/v1/auth/login", "/error").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    // Declaramos el PasswordEncoder aquí para que el AuthService pueda usarlo
    // y comparar la contraseña en texto plano con la encriptada
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}