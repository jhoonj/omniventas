package com.omniventas.formula.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FormulaRepository {
    Mono<FormulaMedica> save(FormulaMedica formula);
    Mono<FormulaMedica> findById(Long id);
    Flux<FormulaMedica> findAll();
    Mono<Void> deleteById(Long id);
}