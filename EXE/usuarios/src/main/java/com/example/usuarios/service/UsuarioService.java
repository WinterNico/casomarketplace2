package com.example.usuarios.service;

import com.example.usuarios.dto.RegistroRequest;
import com.example.usuarios.model.Rol;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.RolRepository;
import com.example.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Encriptador para cumplir con seguridad
    }

    public Usuario registrarUsuario(RegistroRequest request) {
        // Validamos si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Buscamos el rol para asignar privilegios diferentes [cite: 15]
        Rol rol = rolRepository.findByNombre(request.getNameRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());

        // Encriptamos la contraseña antes de almacenarla
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoles(rol);

        return usuarioRepository.save(usuario);
    }
}
