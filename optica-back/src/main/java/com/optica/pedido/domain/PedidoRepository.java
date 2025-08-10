package com.optica.pedido.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PedidoRepository {
    Mono<Pedido> save(Pedido pedido);
    Mono<Pedido> findById(Long id);
    Flux<Pedido> findAll();
    Mono<Void> deleteById(Long id);
}