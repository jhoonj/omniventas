package com.omniventas.producto.domain;

import java.util.UUID;

public record InventarioSaldo(
        UUID productoUid,
        UUID almacenId,   // puede ser null si no usas multi-almacén
        long saldo
) {}
