package com.omniventas.producto.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoRepository {
    Mono<Producto> save(Producto producto);
    Mono<Producto> findById(Long id);
    Flux<Producto> findAll();
    Mono<Void> deleteById(Long id);
}