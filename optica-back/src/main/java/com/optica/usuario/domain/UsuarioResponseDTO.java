package com.optica.usuario.domain;

public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String email,
        Long rol_id
) {}