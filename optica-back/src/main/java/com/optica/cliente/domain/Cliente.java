package com.optica.cliente.domain;

public record Cliente(
    Long id,
    String nombre,
    String email,
    String telefono,
    String direccion
) {}