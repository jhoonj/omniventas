package com.optica.proveedor.domain;

public record Proveedor(
    Long id,
    String nombre,
    String email,
    String telefono,
    String direccion
) {}