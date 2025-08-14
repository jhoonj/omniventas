package com.omniventas.producto.infrastructure.dto;

import com.omniventas.producto.domain.Producto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductoRes(
        UUID uid,
        String nombre,
        String descripcion,
        String tipo,
        BigDecimal precio,
        Long proveedorId,
        Instant updatedAt,
        Integer stock
) {
    public static ProductoRes of(Producto p) {
        return new ProductoRes(
                p.uid(),
                p.nombre(),
                p.descripcion(),
                p.tipo(),
                p.precio(),
                p.proveedorId(),
                p.updatedAt(),
                p.stock()
        );
    }
}
