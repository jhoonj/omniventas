package com.omniventas.producto.domain;


public record ImagenMeta(
        String filename,
        String contentType,
        String url // puede ser http(s) o relativo /api/...
) {}