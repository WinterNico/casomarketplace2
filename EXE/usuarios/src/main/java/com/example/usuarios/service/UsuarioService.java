package com.example.usuarios.service;

import java.time.LocalDateTime;
import java.util.Collections;
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
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo " + request.getEmail() + " ya se encuentra registrado en el sistema.");
        }

        Rol rol = rolRepository.findByNombre(request.getNameRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = new Usuario();

        // Seteamos los datos básicos que vienen del DTO
        usuario.setName(request.getName());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getPhone() != null) {
            usuario.setPhone(request.getPhone());
        }

        // Seteamos la fecha automática de registro (es obligatorio según tu entidad)
        usuario.setRegistrationDate(LocalDateTime.now());

        // Nos aseguramos de que entre como activo
        usuario.setActivo(true);

        // ¡AQUÍ ESTÁ LA MAGIA PARA EL SET DE ROLES!
        // Envolvemos el rol único en un Set para que Spring no tire el error de Cast
        usuario.setRoles(Collections.singleton(rol));

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
