package com.omniventas.producto.infrastructure;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface R2dbcProductoRepository extends ReactiveCrudRepository<R2dbcProductoEntity, Long> {
    Mono<R2dbcProductoEntity> findByUid(UUID uid);
    Mono<Void> deleteByUid(UUID uid);
}
