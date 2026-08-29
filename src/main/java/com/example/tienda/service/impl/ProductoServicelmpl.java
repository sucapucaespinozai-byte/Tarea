package com.example.tienda.service.impl;

import com.example.tienda.dto.ProductoRequestDTO;
import com.example.tienda.dto.ProductoResponseDTO;
import com.example.tienda.entity.Categoria;
import com.example.tienda.entity.Producto;
import com.example.tienda.exception.RecursosNoEncontradoException;
import com.example.tienda.mapper.ProductoMapper;
import com.example.tienda.repository.CategoriaRepository;
import com.example.tienda.repository.ProductoRepository;
import com.example.tienda.service.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServicelmpl  implements  ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @Override
    public List<ProductoResponseDTO> listarProductos() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Producto no encontrado con ID: " + id));
        return productoMapper.toResponseDTO(producto);
    }

    @Override
    public ProductoResponseDTO guardarProducto(ProductoRequestDTO requestDTO) {
        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> new RecursosNoEncontradoException("Categoría no encontrada con ID: " + requestDTO.getCategoriaId()));

        Producto producto = new Producto();
        producto.setNombre(requestDTO.getNombre());
        producto.setPrecio(requestDTO.getPrecio());
        producto.setStock(requestDTO.getStock());
        producto.setCategoria(categoria);

        Producto productoGuardado = productoRepository.save(producto);
        return productoMapper.toResponseDTO(productoGuardado);
    }

    @Override
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO requestDTO) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Producto no encontrado con ID: " + id));

        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> new RecursosNoEncontradoException("Categoría no encontrada con ID: " + requestDTO.getCategoriaId()));

        productoExistente.setNombre(requestDTO.getNombre());
        productoExistente.setPrecio(requestDTO.getPrecio());
        productoExistente.setStock(requestDTO.getStock());
        productoExistente.setCategoria(categoria);

        Producto productoActualizado = productoRepository.save(productoExistente);
        return productoMapper.toResponseDTO(productoActualizado);
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Producto no encontrado con ID: " + id));
        productoRepository.delete(producto);
    }
}