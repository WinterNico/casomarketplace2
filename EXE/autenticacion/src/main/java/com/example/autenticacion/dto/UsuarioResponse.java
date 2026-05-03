package com.example.autenticacion.dto;

import java.util.List;

public class UsuarioResponse {
    private Long id;
    private String email;
    private String password;

    // ¡AQUÍ ESTÁ LA CLAVE! Es una Lista y se llama "roles"
    private List<RolResponse> roles;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<RolResponse> getRoles() { return roles; }
    public void setRoles(List<RolResponse> roles) { this.roles = roles; }

    // La mini-clase para atrapar el nombre adentro de la lista
    public static class RolResponse {
        private String nombre;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }
}