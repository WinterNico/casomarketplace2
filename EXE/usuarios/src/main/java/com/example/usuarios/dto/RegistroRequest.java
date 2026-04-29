package com.example.usuarios.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


// ESTA CLASE ES PARA HACER REGISTRO DE LO QUE RECIBE
@Getter
@Setter
public class RegistroRequest {
    private String name;
    private String email;
    private String password;
    private String nameRol;
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNameRol() {
        return nameRol;
    }

    public void setNameRol(String nameRol) {
        this.nameRol = nameRol;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
