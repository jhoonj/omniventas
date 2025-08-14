package com.omniventas.producto.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Producto(
        Long id,              // interno (BIGINT)
        UUID uid,             // público (UUID)
        String nombre,
        String descripcion,
        String tipo,          // ej: "lente", "armazon", etc. (opcional)
        BigDecimal precio,    // numeric(10,2)
        Long proveedorId,     // FK a proveedores.id (opcional)
        Instant createdAt,
        Instant updatedAt,
        String imagen_url,
        Integer stock
) {
    public Producto withIds(Long id, UUID uid) {
        return new Producto(id, uid, nombre, descripcion, tipo, precio, proveedorId, createdAt, updatedAt,imagen_url, stock);
    }
}
