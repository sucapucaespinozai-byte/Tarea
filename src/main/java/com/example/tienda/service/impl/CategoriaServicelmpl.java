package com.example.tienda.service.impl;

import com.example.tienda.dto.CategoriaRequestDTO;
import com.example.tienda.dto.CategoriaResponseDTO;
import com.example.tienda.entity.Categoria;
import com.example.tienda.exception.RecursosNoEncontradoException;
import com.example.tienda.repository.CategoriaRepository;
import com.example.tienda.service.service.CategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServicelmpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaServicelmpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    private CategoriaResponseDTO convertirADto(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setActivo(categoria.getActivo());
        return dto;
    }

    @Override
    public List<CategoriaResponseDTO> listar() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoriaResponseDTO buscar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Categoría no encontrada con ID: " + id));
        return convertirADto(categoria);
    }

    @Override
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setActivo(dto.getActivo());

        Categoria guardada = categoriaRepository.save(categoria);
        return convertirADto(guardada);
    }

    @Override
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Categoría no encontrada con ID: " + id));

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setActivo(dto.getActivo());

        Categoria actualizada = categoriaRepository.save(existente);
        return convertirADto(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Categoría no encontrada con ID: " + id));
        categoriaRepository.delete(existente);
    }
}