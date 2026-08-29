package com.example.tienda.service.service;

import com.example.tienda.dto.ProductoRequestDTO;
import com.example.tienda.dto.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {
    List<ProductoResponseDTO> listarProductos();
    ProductoResponseDTO obtenerPorId(Long id);
    ProductoResponseDTO guardarProducto(ProductoRequestDTO requestDTO);
    ProductoResponseDTO actualizar(Long id, ProductoRequestDTO requestDTO);
    void eliminar(Long id);
}