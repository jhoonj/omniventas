package com.omniventas.proveedor.infrastructure;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface R2dbcProveedorRepository extends ReactiveCrudRepository<R2dbcProveedorEntity, Long> {
    Mono<R2dbcProveedorEntity> findByUid(UUID uid);
    Mono<Boolean> existsByEmail(String email);       // CITEXT = case-insensitive
    Mono<Void> deleteByUid(UUID uid);
}
