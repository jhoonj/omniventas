package com.omniventas.rol.infrastructure;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("roles")
public class R2dbcRolEntity {
    @Id
    private Long id;
    private String nombre;
}
