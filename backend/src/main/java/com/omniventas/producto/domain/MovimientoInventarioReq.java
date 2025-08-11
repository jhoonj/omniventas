package com.omniventas.producto.domain;

import java.util.UUID;

public record MovimientoInventarioReq(
        String tipo,                 // ingreso | salida | reserva | ajuste | transferencia_in | transferencia_out
        int cantidad,                // positivo; el signo se ajusta según el tipo (excepto ajuste)
        String referenciaTipo,       // opcional (pedido, ajuste-manual, etc.)
        UUID referenciaId,           // opcional (puede ser pedido_uid)
        String nota,                 // opcional
        UUID almacenId               // opcional
) {}
