package com.example.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tienda.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}