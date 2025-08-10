package com.optica.rol.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Rol> save(Rol rol);           // crea o actualiza (upsert por id)
    Mono<Rol> findById(Long id);
    Flux<Rol> findAll();
    Mono<Void> deleteById(Long id);

    Mono<Rol> findByNombre(String nombre); // útil para validar unicidad / lookups
}
