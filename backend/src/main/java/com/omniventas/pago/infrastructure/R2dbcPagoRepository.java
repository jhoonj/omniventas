package com.omniventas.pago.infrastructure;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface R2dbcPagoRepository extends ReactiveCrudRepository<R2dbcPagoEntity, Long> {}