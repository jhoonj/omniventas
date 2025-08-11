package com.omniventas.detalle.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DetallePedidoRepository {
    Mono<DetallePedido> save(DetallePedido detalle);
    Mono<DetallePedido> findById(Long id);
    Flux<DetallePedido> findAll();
    Mono<Void> deleteById(Long id);
}