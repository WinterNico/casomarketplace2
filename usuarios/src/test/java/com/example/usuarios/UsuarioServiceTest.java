package com.example.usuarios;

import com.example.usuarios.dto.RegistroRequest;
import com.example.usuarios.model.Rol;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.RolRepository;
import com.example.usuarios.repository.UsuarioRepository;
import com.example.usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private RegistroRequest requestPrueba;
    private Rol rolPrueba;
    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        requestPrueba = new RegistroRequest();
        requestPrueba.setName("Ren Amamiya");
        requestPrueba.setEmail("joker@phantom.cl");
        requestPrueba.setPassword("PasswordSecreta123!");
        requestPrueba.setNameRol("ROLE_CLIENTE");
        requestPrueba.setPhone("+56999887766");

        rolPrueba = new Rol();
        rolPrueba.setId(1L);
        rolPrueba.setNombre("ROLE_CLIENTE");

        usuarioPrueba = new Usuario();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setEmail("joker@phantom.cl");
        usuarioPrueba.setName("Ren Amamiya");
    }

    @Test
    void registrarUsuario_Exitoso() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findByNombre(anyString())).thenReturn(Optional.of(rolPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        Usuario resultado = usuarioService.registrarUsuario(requestPrueba);

        assertNotNull(resultado);
        assertEquals("Ren Amamiya", resultado.getName());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_LanzaErrorCuandoCorreoExiste() {
        when(usuarioRepository.existsByEmail("joker@phantom.cl")).thenReturn(true);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(requestPrueba);
        });

        assertEquals("El correo ya se encuentra registrado en el sistema.", excepcion.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_LanzaErrorCuandoRolNoExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findByNombre(anyString())).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(requestPrueba);
        });

        assertEquals("Rol no encontrado: ROLE_CLIENTE", excepcion.getMessage());
    }

    @Test
    void buscarPorEmail_Exitoso() {
        when(usuarioRepository.findByEmail("joker@phantom.cl")).thenReturn(Optional.of(usuarioPrueba));
        Usuario resultado = usuarioService.buscarPorEmail("joker@phantom.cl");
        assertNotNull(resultado);
    }

    @Test
    void buscarPorEmail_LanzaErrorCuandoNoExiste() {
        when(usuarioRepository.findByEmail("joker@phantom.cl")).thenReturn(Optional.empty());
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.buscarPorEmail("joker@phantom.cl");
        });
        assertTrue(excepcion.getMessage().contains("Usuario no encontrado con el correo"));
    }

    @Test
    void buscarPorId_Exitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));
        Usuario resultado = usuarioService.buscarPorId(1L);
        assertNotNull(resultado);
    }

    @Test
    void buscarPorId_LanzaErrorCuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.buscarPorId(99L);
        });
        assertEquals("Usuario no encontrado con el ID: 99", excepcion.getMessage());
    }
}