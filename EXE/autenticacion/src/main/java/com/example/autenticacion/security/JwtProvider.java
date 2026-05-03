package com.example.autenticacion.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // Esto le dice a Spring: "Guarda esta fábrica para que la podamos usar en otras partes"
public class JwtProvider {

    // 1. LA CLAVE SECRETA: Es la firma de tu API.
    // Si alguien no tiene esta clave exacta, no puede falsificar tus tokens.
    // OJO: Para el algoritmo HS256, la clave debe tener mínimo 32 caracteres (256 bits).
    private final String jwtSecret = "EstaEsUnaClaveSecretaSuperSeguraParaElMarketplaceDuoc2026!";

    // 2. EL TIEMPO DE VIDA: ¿Cuánto dura el token antes de vencer?
    // Aquí le pusimos 1 hora (3600000 milisegundos).
    private final int jwtExpirationMs = 3600000;

    // 3. LA FÁBRICA: El método que recibe los datos y escupe el token
    public String generarToken(String email, Long idUsuario, String rol) {

        // Transformamos tu clave secreta de texto a una "Llave Criptográfica" real
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        // Aquí armamos el JSON Web Token
        return Jwts.builder()
                .setSubject(email)                           // El sujeto principal (el correo)
                .claim("id", idUsuario)                      // Dato extra: El ID (útil para el frontend)
                .claim("rol", rol)                           // Dato extra: El Rol (Cliente, Vendedor, etc.)
                .setIssuedAt(new Date())                     // Fecha de emisión (Ahora mismo)
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Fecha de caducidad
                .signWith(key, SignatureAlgorithm.HS256)     // Firmamos el token con la llave y el algoritmo
                .compact();                                  // Lo empaquetamos todo en ese String larguísimo
    }
}

