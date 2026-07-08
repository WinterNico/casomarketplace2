package com.example.autenticacion.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {


    private final String jwtSecret = "EstaEsUnaClaveSecretaSuperSeguraParaElMarketplaceDuoc2026!";


    private final int jwtExpirationMs = 86400000; // 1 dia, para 1 hora son 3600000

    public String generarToken(String email, Long idUsuario, String rol) {

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(email)
                .claim("id", idUsuario)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

