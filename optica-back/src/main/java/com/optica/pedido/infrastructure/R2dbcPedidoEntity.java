package com.optica.pedido.infrastructure;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("pedidos")
public class R2dbcPedidoEntity {
    @Id
    private Long id;
    private Long clienteId;
    private LocalDateTime fechaPedido;
    private String estado;
    private Double total;
    private Long formulaId;

    // Getters y setters
}