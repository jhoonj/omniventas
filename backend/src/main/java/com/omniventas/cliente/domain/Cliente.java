package com.omniventas.cliente.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Modelo de dominio: Cliente
 * id: interno (BIGINT DB)
 * uid: público (UUID API)
 */
public record Cliente(
        Long id,
        UUID uid,
        String nombre,
        String email,
        String telefono,
        String direccion,
        Instant createdAt,
        Instant updatedAt
) {
    public Cliente withIds(Long id, UUID uid) {
        return new Cliente(
                id,
                uid,
                this.nombre,
                this.email,
                this.telefono,
                this.direccion,
                this.createdAt,
                this.updatedAt
        );
    }
}
