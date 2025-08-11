package com.omniventas.rol.infrastructure;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface R2dbcRolRepository extends ReactiveCrudRepository<R2dbcRolEntity, Long> {
    Mono<R2dbcRolEntity> findByNombre(String nombre);
}
