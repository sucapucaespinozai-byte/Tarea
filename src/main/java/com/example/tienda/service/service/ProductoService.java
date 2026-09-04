package com.example.tienda.service.service;

import com.example.tienda.dto.ProductoRequestDTO;
import com.example.tienda.dto.ProductoResponseDTO;
import com.example.tienda.service.generic.CrudService;

public interface ProductoService extends CrudService<ProductoRequestDTO, ProductoResponseDTO, Long> {
}