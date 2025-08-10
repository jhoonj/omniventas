package com.optica.usuario.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R2dbcUsuarioEntity {
    @Id
    private Long id;
    private String nombre;
    private String email;
    private Long rol_id;
    private String contrasenaHash;

}