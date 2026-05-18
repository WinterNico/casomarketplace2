package com.example.busqueda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String state;
    private BigDecimal total;
    private LocalDateTime creationDate;
    private String status;
    private String trackingNumber;
}