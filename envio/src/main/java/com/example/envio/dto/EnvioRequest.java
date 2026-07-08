package com.example.envio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioRequest {

    @NotNull(message ="El id es obligatorio")
    private Long id;
}
