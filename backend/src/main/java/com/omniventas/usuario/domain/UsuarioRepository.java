package com.omniventas.usuario.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Usuario> save(Usuario usuario);
    Mono<Usuario> findById(Long id);
    Flux<Usuario> findAll();
    Mono<Void> deleteById(Long id);
    Mono<Usuario> findByEmail(String email);
}