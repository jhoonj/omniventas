package com.omniventas.producto.domain;

import java.util.List;
import java.util.UUID;

public record StockRes(
        UUID productoUid,
        int total,
        List<InventarioSaldo> porAlmacen
) {}
