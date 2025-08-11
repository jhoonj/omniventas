package com.omniventas.proveedor.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProveedorRepository {
    Mono<Proveedor> save(Proveedor proveedor);
    Mono<Proveedor> findById(Long id);
    Flux<Proveedor> findAll();
    Mono<Void> deleteById(Long id);
}