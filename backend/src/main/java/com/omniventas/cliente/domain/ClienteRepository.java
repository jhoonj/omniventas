package com.omniventas.cliente.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteRepository {
    Mono<Cliente> save(Cliente cliente);
    Mono<Cliente> findById(Long id);
    Flux<Cliente> findAll();
    Mono<Void> deleteById(Long id);
}