package com.optica.formula.domain;

import java.time.LocalDate;

public record FormulaMedica(
    Long id,
    Long clienteId,
    LocalDate fechaEmision,
    Double odEsfera,
    Double odCilindro,
    Integer odEje,
    Double oiEsfera,
    Double oiCilindro,
    Integer oiEje,
    Double distanciaInterpupilar,
    String observaciones
) {}