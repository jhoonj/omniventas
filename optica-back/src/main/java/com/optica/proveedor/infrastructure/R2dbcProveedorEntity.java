package com.optica.proveedor.infrastructure;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("proveedores")
public class R2dbcProveedorEntity {
    @Id
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;

    // Getters y setters
}