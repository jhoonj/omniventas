package com.optica.producto.infrastructure;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface R2dbcProductoRepository extends ReactiveCrudRepository<R2dbcProductoEntity, Long> {}