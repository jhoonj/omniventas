package com.omniventas.proveedor.domain;

import java.time.Instant;
import java.util.UUID;

public record Proveedor(
        Long id,          // interno (BIGINT)
        UUID uid,         // público (UUID)
        String nombre,
        String email,     // CITEXT en DB -> String aquí
        String telefono,
        String direccion,
        Instant createdAt,
        Instant updatedAt
) {
    public Proveedor withIds(Long id, UUID uid) {
        return new Proveedor(id, uid, nombre, email, telefono, direccion, createdAt, updatedAt);
    }
}
