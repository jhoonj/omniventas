package com.optica.pago.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PagoRepository {
    Mono<Pago> save(Pago pago);
    Mono<Pago> findById(Long id);
    Flux<Pago> findAll();
    Mono<Void> deleteById(Long id);
}