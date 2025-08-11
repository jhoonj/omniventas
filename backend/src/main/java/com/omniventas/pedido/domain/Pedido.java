package com.omniventas.pedido.domain;

import java.time.LocalDateTime;

public record Pedido(
    Long id,
    Long clienteId,
    LocalDateTime fechaPedido,
    String estado,
    Double total,
    Long formulaId
) {}