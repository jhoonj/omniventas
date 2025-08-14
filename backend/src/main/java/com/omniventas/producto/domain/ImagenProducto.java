package com.omniventas.producto.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Modelo de dominio para imágenes de producto (agnóstico a infraestructura). */
public record ImagenProducto(
        Long id,
        UUID uid,
        UUID productoUid,
        String filename,
        String contentType,
        long sizeBytes,
        boolean principal,
        String altText,
        String url,                 // si es imagen remota (opcional), local si null
        OffsetDateTime createdAt
) {}
