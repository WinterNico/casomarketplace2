package com.example.usuarios.config;

import com.example.usuarios.model.Rol;
import com.example.usuarios.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RolRepository rolRepository) {
        return args -> {
            // Verificamos si el rol no existe antes de guardarlo para evitar duplicados
            if (rolRepository.findByNombre("ROLE_CLIENTE").isEmpty()) {
                rolRepository.save(new Rol("ROLE_CLIENTE"));
                System.out.println("Rol ROLE_CLIENTE creado automáticamente.");
            }
            if (rolRepository.findByNombre("ROLE_VENDEDOR").isEmpty()) {
                rolRepository.save(new Rol("ROLE_VENDEDOR"));
                System.out.println("Rol ROLE_VENDEDOR creado automáticamente.");
            }
            if (rolRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
                rolRepository.save(new Rol("ROLE_ADMIN"));
                System.out.println("Rol ROLE_ADMIN creado automáticamente.");
            }
        };
    }
}