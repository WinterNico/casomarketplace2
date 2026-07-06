package com.example.usuarios.controller;

import com.example.usuarios.dto.RegistroRequest;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios del sistema")
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Busca y retorna los detalles de un usuario específico mediante su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado en el sistema")
    })
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        log.info("Petición REST para buscar usuario por ID: {}", id);
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.add(linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel());
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @GetMapping("/buscar/email/{email}")
    @Operation(summary = "Obtener usuario por Email", description = "Busca y retorna los detalles de un usuario utilizando su dirección de correo electrónico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el correo proporcionado")
    })
    public ResponseEntity<Usuario> obtenerUsuarioPorEmail(@PathVariable String email) {
        log.info("Petición REST para buscar usuario por email: {}", email);
        Usuario usuario = usuarioService.buscarPorEmail(email);
        // Se le agrega el link HATEOAS apuntando a la búsqueda por ID
        usuario.add(linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel());
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }


    @PostMapping("/registro")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario en la base de datos y encripta su contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la validación de los datos (ej. correo duplicado)")
    })
    public ResponseEntity<Usuario> registrarUsuario(@Valid @RequestBody RegistroRequest request) {
        log.info("Petición REST para registrar un nuevo usuario con email: {}", request.getEmail());
        Usuario nuevoUsuario = usuarioService.registrarUsuario(request);
        nuevoUsuario.add(linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(nuevoUsuario.getId())).withSelfRel());
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }
}