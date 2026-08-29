package com.example.tienda.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer activo;
    private LocalDateTime createdAt;
}