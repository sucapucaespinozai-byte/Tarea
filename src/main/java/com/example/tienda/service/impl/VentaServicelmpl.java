package com.example.tienda.service.impl;

import com.example.tienda.dto.VentaRequestDTO;
import com.example.tienda.dto.VentaResponseDTO;
import com.example.tienda.entity.DetalleVenta;
import com.example.tienda.entity.Venta;
import com.example.tienda.exception.RecursosNoEncontradoException;
import com.example.tienda.exception.ReglaNegocioException;
import com.example.tienda.repository.ClienteRepository;
import com.example.tienda.repository.ProductoRepository;
import com.example.tienda.repository.VentaRepository;
import com.example.tienda.service.service.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentaServicelmpl implements VentaService {
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public VentaServicelmpl(
            VentaRepository ventaRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    private VentaResponseDTO convertirADto(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setTotal(venta.getTotal());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listar() {
        return ventaRepository.findAll().stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO buscar(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Venta no encontrada con ID: " + id));
        return convertirADto(venta);
    }

    @Override
    @Transactional
    public VentaResponseDTO crear(VentaRequestDTO request) {
        var cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursosNoEncontradoException("Cliente no encontrado con ID: " + request.getClienteId()));

        if (Boolean.FALSE.equals(cliente.getEstado())) {
            throw new ReglaNegocioException("El cliente se encuentra inactivo y no puede realizar ventas.");
        }

        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La lista de detalles no puede estar vacía.");
        }

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now());

        BigDecimal totalVenta = BigDecimal.ZERO;

        for (var detalleDto : request.getDetalles()) {
            if (detalleDto.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad del producto debe ser mayor a cero.");
            }

            var producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RecursosNoEncontradoException("Producto no encontrado con ID: " + detalleDto.getProductoId()));

            if (Boolean.FALSE.equals(producto.getEstado())) {
                throw new ReglaNegocioException("El producto se encuentra inactivo y no puede ser vendido.");
            }

            if (producto.getStock() < detalleDto.getCantidad()) {
                throw new ReglaNegocioException("Stock insuficiente para el producto con ID: " + producto.getId());
            }

            BigDecimal precioUnitario = producto.getPrecio();
            int cantidad = detalleDto.getCantidad();
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

            // DESCUENTO DE STOCK
            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecio(precioUnitario);
            detalle.setSubtotal(subtotal);

            venta.agregarDetalle(detalle);
            totalVenta = totalVenta.add(subtotal);
        }

        venta.setTotal(totalVenta);

        Venta guardada = ventaRepository.save(venta);
        return convertirADto(guardada);
    }

    @Override
    @Transactional
    public VentaResponseDTO actualizar(Long id, VentaRequestDTO request) {
        Venta existente = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Venta no encontrada con ID: " + id));

        Venta actualizada = ventaRepository.save(existente);
        return convertirADto(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Venta existente = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Venta no encontrada con ID: " + id));
        ventaRepository.delete(existente);
    }
}