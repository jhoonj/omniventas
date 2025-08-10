package com.optica.pedido.infrastructure;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface R2dbcPedidoRepository extends ReactiveCrudRepository<R2dbcPedidoEntity, Long> {}