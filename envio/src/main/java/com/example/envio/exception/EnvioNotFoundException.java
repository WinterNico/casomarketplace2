package com.example.envio.exception;

public class EnvioNotFoundException extends RuntimeException {
    public EnvioNotFoundException(String message) {
        super(message);
    }
}