package com.omniventas.cliente.infrastructure;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface R2dbcClienteRepository extends ReactiveCrudRepository<R2dbcClienteEntity, Long> {
    Mono<R2dbcClienteEntity> findByUid(UUID uid);
    Mono<Boolean> existsByEmail(String email); // CITEXT hace match case-insensitive en DB
    Mono<Void> deleteByUid(UUID uid);
}
