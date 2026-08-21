package com.example.tienda.service.impl;

import com.example.tienda.dto.CategoriaRequestDTO;
import com.example.tienda.dto.CategoriaResponseDTO;
import com.example.tienda.entity.Categoria;
import com.example.tienda.exception.RecursosNoEncontradoException;
import com.example.tienda.exception.ReglaNegocioException;
import com.example.tienda.repository.CategoriaRepository;
import com.example.tienda.service.service.CategoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class CategoriaServicelmpl implements  CategoriaService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoriaServicelmpl.class);

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
        dto.setCreatedAt(categoria.getCreatedAt());
        return dto;
    }

    @Override
    public List<CategoriaResponseDTO> listar() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoriaResponseDTO buscarPorId(Long aLong) {
        Categoria categoria = categoriaRepository.findById(aLong)
                .orElseThrow(() -> new RecursosNoEncontradoException("Categoría no encontrada con ID: " + aLong));
        return convertirADto(categoria);
    }

    @Override
    public CategoriaResponseDTO registrar(CategoriaRequestDTO categoriaRequestDTO) {
        if (categoriaRepository.findByNombre(categoriaRequestDTO.getNombre()).isPresent()) {
            throw new ReglaNegocioException("Ya existe una categoría con el nombre: " + categoriaRequestDTO.getNombre());
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaRequestDTO.getNombre());
        categoria.setDescripcion(categoriaRequestDTO.getDescripcion());
        categoria.setActivo(categoriaRequestDTO.getActivo());
        Categoria guardada = categoriaRepository.save(categoria);
        return convertirADto(guardada);
    }

    @Override
    public CategoriaResponseDTO actualizar(Long aLong, CategoriaRequestDTO categoriaRequestDTO) {
        Categoria existente = categoriaRepository.findById(aLong)
                .orElseThrow(() -> new RecursosNoEncontradoException("Categoría no encontrada con ID: " + aLong));

        categoriaRepository.findByNombre(categoriaRequestDTO.getNombre()).ifPresent(cat -> {
            if (!cat.getId().equals(aLong)) {
                throw new ReglaNegocioException("Ya existe otra categoría con el nombre: " + categoriaRequestDTO.getNombre());
            }
        });

        existente.setNombre(categoriaRequestDTO.getNombre());
        existente.setDescripcion(categoriaRequestDTO.getDescripcion());
        existente.setActivo(categoriaRequestDTO.getActivo());
        Categoria actualizada = categoriaRepository.save(existente);
        return convertirADto(actualizada);
    }

    @Override
    public void eliminar(Long aLong) {
        Categoria existente = categoriaRepository.findById(aLong)
                .orElseThrow(() -> new RecursosNoEncontradoException("Categoría no encontrada con ID: " + aLong));
        categoriaRepository.delete(existente);
    }
}
