package com.example.pedidos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PedidoRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser mayor a cero")
    private BigDecimal total;

    @NotNull(message = "La tarjeta es obligatoria")
    private String tarjeta;


    //Costructores vacios y con parametros

    public PedidoRequestDTO(){}

    //getters y setters
    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTarjeta() {
        return tarjeta;
    }
    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }
}
