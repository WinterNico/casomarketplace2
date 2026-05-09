package com.example.pagos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Collections;
import org.slf4j.Logger;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    // ¡CRÍTICO! Esta clave TIENE que ser exactamente la misma que pusiste en ms-autenticacion
    private final String jwtSecret = "EstaEsUnaClaveSecretaSuperSeguraParaElMarketplaceDuoc2026!";
    private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Buscamos el token en la cabecera de la petición
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Le cortamos la palabra "Bearer "

            try {
                // 2. Preparamos la llave para abrirlo
                Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

                // 3. Abrimos el token y sacamos los datos que guardamos antes
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String email = claims.getSubject();
                String rol = claims.get("rol", String.class);

                // 4. Le avisamos a Spring Security que el usuario es válido
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(rol))
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Si el token fue modificado por un hacker o ya caducó, se ignora
                log.warn("Token inválido o expirado: " + e.getMessage());
            }
        }

        // 5. Dejamos que la petición siga su curso hacia el Controlador
        filterChain.doFilter(request, response);
    }
}