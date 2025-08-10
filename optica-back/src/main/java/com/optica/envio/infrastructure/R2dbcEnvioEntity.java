package com.optica.envio.infrastructure;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Table("envios")
public class R2dbcEnvioEntity {
    @Id
    private Long id;
    private Long pedidoId;
    private String direccionEnvio;
    private String empresaEnvio;
    private String numeroGuia;
    private String estadoEnvio;
    private LocalDate fechaEnvio;
    private LocalDate fechaEntregaEstimada;

    // Getters y setters
}