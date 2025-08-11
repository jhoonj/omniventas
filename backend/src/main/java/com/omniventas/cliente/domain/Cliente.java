package com.omniventas.cliente.domain;

public record Cliente(
    Long id,
    String nombre,
    String email,
    String telefono,
    String direccion
) {}