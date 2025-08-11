package com.omniventas.pago.infrastructure;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("pagos")
public class R2dbcPagoEntity {
    @Id
    private Long id;
    private Long pedidoId;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private Double monto;
    private String estado;

    // Getters y setters
}