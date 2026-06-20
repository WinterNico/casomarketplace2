package com.example.autenticacion.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // Se guarda para ocuparlo en otras partes
public class JwtProvider {

    // Clave 256
    private final String jwtSecret = "EstaEsUnaClaveSecretaSuperSeguraParaElMarketplaceDuoc2026!";

    // Tiempo del TOKEN
    private final int jwtExpirationMs = 86400000; // 1 dia, para 1 hora son 3600000

    // Este metodo recibe el token tmb
    public String generarToken(String email, Long idUsuario, String rol) {

        // Se transforma la clave
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        // Se arma el json web token
        return Jwts.builder()
                .setSubject(email)
                .claim("id", idUsuario)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Fecha de caducidad
                .signWith(key, SignatureAlgorithm.HS256)     // Firmamos el token con la llave y el algoritmo
                .compact();                                  // Lo empaquetamos todo en ese string de 256
    }
}

