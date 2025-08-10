package com.optica.envio.domain;

import java.time.LocalDate;

public record Envio(
    Long id,
    Long pedidoId,
    String direccionEnvio,
    String empresaEnvio,
    String numeroGuia,
    String estadoEnvio,
    LocalDate fechaEnvio,
    LocalDate fechaEntregaEstimada
) {}