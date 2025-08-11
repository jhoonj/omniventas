package com.omniventas.envio.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EnvioRepository {
    Mono<Envio> save(Envio envio);
    Mono<Envio> findById(Long id);
    Flux<Envio> findAll();
    Mono<Void> deleteById(Long id);
}