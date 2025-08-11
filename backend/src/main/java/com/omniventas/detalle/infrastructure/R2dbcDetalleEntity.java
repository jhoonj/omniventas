package com.omniventas.detalle.infrastructure;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("detalle_pedidos")
public class R2dbcDetalleEntity {
    @Id
    private Long id;
    @Column("pedido_id")
    private Long pedidoId;

    @Column("producto_id")
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;

    // Getters y setters
}