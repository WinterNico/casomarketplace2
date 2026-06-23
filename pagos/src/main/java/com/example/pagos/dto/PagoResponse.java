package com.example.pagos.dto;

public class PagoResponse {
    private String transaccionId;
    private String estado;
    private String mensaje;

    public PagoResponse(String transaccionId, String estado, String mensaje) {
        this.transaccionId = transaccionId;
        this.estado = estado;
        this.mensaje = mensaje;
    }

    // Getters
    public String getTransaccionId() { return transaccionId; }
    public String getEstado() { return estado; }
    public String getMensaje() { return mensaje; }
}