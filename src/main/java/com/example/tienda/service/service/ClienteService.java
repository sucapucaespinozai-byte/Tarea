package com.example.tienda.service.service;

import com.example.tienda.dto.ClienteRequestDTO;
import com.example.tienda.dto.ClienteResponseDTO;
import com.example.tienda.service.generic.CrudService;

public interface ClienteService extends CrudService<ClienteRequestDTO, ClienteResponseDTO, Long> {
}