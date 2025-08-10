package com.optica.pago.domain;

import java.time.LocalDateTime;

public record Pago(
    Long id,
    Long pedidoId,
    LocalDateTime fechaPago,
    String metodoPago,
    Double monto,
    String estado
) {}