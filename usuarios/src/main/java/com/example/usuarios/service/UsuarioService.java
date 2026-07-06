package com.example.usuarios.service;

import java.util.Collections;

import com.example.usuarios.dto.RegistroRequest;
import com.example.usuarios.model.Rol;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.RolRepository;
import com.example.usuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Usuario registrarUsuario(RegistroRequest request) {
        log.info("Comprobando si el correo {} ya existe...", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.error("Fallo de registro: El correo {} ya está en uso", request.getEmail());
            throw new RuntimeException("El correo ya se encuentra registrado en el sistema.");
        }

        Rol rol = rolRepository.findByNombre(request.getNameRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + request.getNameRol()));

        Usuario usuario = new Usuario();
        usuario.setName(request.getName());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getPhone() != null) {
            usuario.setPhone(request.getPhone());
        }

        usuario.setRoles(Collections.singleton(rol));

        log.info("Guardando nuevo usuario en la base de datos...");
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo: " + email));
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
    }
}