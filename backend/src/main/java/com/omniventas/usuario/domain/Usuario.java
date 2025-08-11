package com.omniventas.usuario.domain;

public record Usuario(
    Long id,
    String nombre,
    String email,
    Long rol_id,
    String contrasenaHash
) {}