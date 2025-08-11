package com.omniventas.formula.infrastructure;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Table("formulas_medicas")
public class R2dbcFormulaEntity {
    @Id
    private Long id;
    private Long clienteId;
    private LocalDate fechaEmision;
    private Double odEsfera;
    private Double odCilindro;
    private Integer odEje;
    private Double oiEsfera;
    private Double oiCilindro;
    private Integer oiEje;
    private Double distanciaInterpupilar;
    private String observaciones;

    // Getters y setters
}