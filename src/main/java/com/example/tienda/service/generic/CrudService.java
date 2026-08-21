package com.example.tienda.service.generic;

import java.util.List;

public interface CrudService<T, ID, ReqDTO, ResDTO> {
    List<ResDTO> listar();
    ResDTO buscarPorId(ID id);
    ResDTO registrar(ReqDTO dto);
    ResDTO actualizar(ID id, ReqDTO dto);
    void eliminar(ID id);
}