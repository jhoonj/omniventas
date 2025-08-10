package com.optica.producto.infrastructure;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("productos")
public class R2dbcProductoEntity {
    @Id
    private Long id;
    private String nombre;
    private String descripcion;
    private String tipo;
    private Double precio;
    private Integer stock;
    private Long proveedorId;

    // Getters y setters
}