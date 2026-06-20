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

    // Se inyecta al service
    @InjectMocks
    private UsuarioService usuarioService;

    // Variables de prueba que usaremos en varios tests
    private RegistroRequest requestPrueba;
    private Rol rolPrueba;
    private Usuario usuarioPrueba;

    // se ejecuta ANTES de cada @Test para preparar los datos
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

    // Registro exitoso
    @Test
    void registrarUsuario_Exitoso() {
        // "Cuando te pregunten si existe el correo, di que NO (false)"
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        // "Cuando busquen el rol ROLE_CLIENTE, entrégales el rol de prueba"
        when(rolRepository.findByNombre(anyString())).thenReturn(Optional.of(rolPrueba));

        // "Cuando intenten guardar el usuario, devuelve el usuario de prueba"
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        // Ejecutamos el método real
        Usuario resultado = usuarioService.registrarUsuario(requestPrueba);

        // (Las Comprobaciones)
        assertNotNull(resultado); // Comprobamos que no devolvió un nulo
        assertEquals("Ren Amamiya", resultado.getName()); // Comprobamos que el nombre coincide

        // Verificamos que el repositorio fingido fue llamado exactamente 1 vez para guardar
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    // Correo ya existe
    @Test
    void registrarUsuario_LanzaErrorCuandoCorreoExiste() {
        // "Cuando pregunten si el correo existe, di que SÍ (true)"
        when(usuarioRepository.existsByEmail("joker@phantom.cl")).thenReturn(true);

        // Comprobamos que al intentar registrar, el servicio se defiende lanzando la excepción exacta
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(requestPrueba);
        });

        // Verificamos que el mensaje de la excepción es el que tú escribiste en el código
        assertEquals("El correo ya se encuentra registrado en el sistema.", excepcion.getMessage());

        // Comprobamos que NUNCA se intentó guardar nada en la base de datos
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // Busqueda por ID
    @Test
    void buscarPorId_LanzaErrorCuandoNoExiste() {
        // "Cuando busquen el ID 99, devuelve un Optional vacío (no existe)"
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.buscarPorId(99L);
        });

        assertEquals("Usuario no encontrado con el ID: 99", excepcion.getMessage());
    }
}
