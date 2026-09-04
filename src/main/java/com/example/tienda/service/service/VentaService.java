package com.example.tienda.service.service;

import com.example.tienda.dto.VentaRequestDTO;
import com.example.tienda.dto.VentaResponseDTO;
import com.example.tienda.service.generic.CrudService;

public interface VentaService extends CrudService<VentaRequestDTO, VentaResponseDTO, Long> {
}