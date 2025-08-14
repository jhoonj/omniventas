package com.omniventas.producto.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoCreateReq(
        String nombre,
        String descripcion,
        String tipo,
        BigDecimal precio,
        UUID proveedorUid
) {}