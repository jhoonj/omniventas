package com.omniventas.producto.domain;

public record Producto(
    Long id,
    String nombre,
    String descripcion,
    String tipo,
    Double precio,
    Integer stock,
    Long proveedorId
) {}