package com.optica.detalle.domain;

public record DetallePedido(
    Long id,
    Long pedidoId,
    Long productoId,
    Integer cantidad,
    Double precioUnitario
) {}