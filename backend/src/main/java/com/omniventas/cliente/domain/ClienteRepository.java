package com.omniventas.cliente.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClienteRepository {
    Mono<Cliente> save(Cliente cliente);                 // create/update
    Mono<Cliente> findByUid(UUID uid);
    Mono<Boolean> existsByEmail(String email);           // citext -> case-insensitive en DB
    Flux<Cliente> findAll();
    Mono<Void> deleteByUid(UUID uid);
}
