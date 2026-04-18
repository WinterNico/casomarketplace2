package com.example.usuarios.dto;

import lombok.Data;


// ESTA CLASE ES PARA HACER REGISTRO DE LO QUE RECIBE
@Data
public class RegistroRequest {
    private String email;
    private String password;
    private String nombre;
    private String telefono;

}
