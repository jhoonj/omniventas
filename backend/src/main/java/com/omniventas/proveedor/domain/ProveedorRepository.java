package com.omniventas.proveedor.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProveedorRepository {
    Mono<Proveedor> save(Proveedor proveedor);     // create/update
    Mono<Proveedor> findByUid(UUID uid);
    Mono<Boolean> existsByEmail(String email);     // CITEXT compara case-insensitive
    Flux<Proveedor> findAll();
    Mono<Void> deleteByUid(UUID uid);
}
