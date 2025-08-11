package com.omniventas.rol.infrastructure;

import com.omniventas.rol.domain.Rol;
import com.omniventas.rol.domain.RolRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcRolAdapter implements RolRepository {

    private final R2dbcRolRepository repo;

    public R2dbcRolAdapter(R2dbcRolRepository repo) {
        this.repo = repo;
    }

    private static Rol toDomain(R2dbcRolEntity e) {
        return new Rol(e.getId(), e.getNombre());
    }

    private static R2dbcRolEntity toEntity(Rol r) {
        var e = new R2dbcRolEntity();
        e.setId(r.id());
        e.setNombre(r.nombre());
        return e;
    }

    @Override
    public Mono<Rol> save(Rol rol) {
        return repo.save(toEntity(rol))
                .map(R2dbcRolAdapter::toDomain);
    }

    @Override
    public Mono<Rol> findById(Long id) {
        return repo.findById(id).map(R2dbcRolAdapter::toDomain);
    }

    @Override
    public Flux<Rol> findAll() {
        return repo.findAll().map(R2dbcRolAdapter::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repo.deleteById(id);
    }

    @Override
    public Mono<Rol> findByNombre(String nombre) {
        return repo.findByNombre(nombre).map(R2dbcRolAdapter::toDomain);
    }
}
