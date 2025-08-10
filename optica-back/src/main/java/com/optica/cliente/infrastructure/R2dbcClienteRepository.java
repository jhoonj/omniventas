package com.optica.cliente.infrastructure;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface R2dbcClienteRepository extends ReactiveCrudRepository<R2dbcClienteEntity, Long> {}