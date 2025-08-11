package com.omniventas.usuario.infrastructure;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface R2dbcUsuarioRepository extends ReactiveCrudRepository<R2dbcUsuarioEntity, Long> {

}