package com.omniventas.proveedor.infrastructure;

import com.omniventas.proveedor.domain.Proveedor;
import com.omniventas.proveedor.domain.ProveedorRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public class R2dbcProveedorAdapter implements ProveedorRepository {

    private final R2dbcProveedorRepository repo;

    public R2dbcProveedorAdapter(R2dbcProveedorRepository repo) {
        this.repo = repo;
    }

    private static Proveedor toDomain(R2dbcProveedorEntity e) {
        if (e == null) return null;
        return new Proveedor(
                e.getId(), e.getUid(), e.getNombre(), e.getEmail(),
                e.getTelefono(), e.getDireccion(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    private static R2dbcProveedorEntity toEntity(Proveedor p) {
        R2dbcProveedorEntity e = new R2dbcProveedorEntity();
        e.setId(p.id());
        e.setUid(p.uid() != null ? p.uid() : UUID.randomUUID());
        e.setNombre(p.nombre());
        e.setEmail(p.email());
        e.setTelefono(p.telefono());
        e.setDireccion(p.direccion());
        e.setCreatedAt(p.createdAt());
        e.setUpdatedAt(p.updatedAt());
        return e;
    }

    @Override
    public Mono<Proveedor> save(Proveedor proveedor) {
        R2dbcProveedorEntity e = toEntity(proveedor);
        if (e.getId() == null) {
            if (e.getUid() == null) e.setUid(UUID.randomUUID());
            if (e.getCreatedAt() == null) e.setCreatedAt(Instant.now());
            e.setUpdatedAt(Instant.now());
        } else {
            e.setUpdatedAt(Instant.now());
        }
        return repo.save(e).map(R2dbcProveedorAdapter::toDomain);
    }

    @Override
    public Mono<Proveedor> findByUid(UUID uid) {
        return repo.findByUid(uid).map(R2dbcProveedorAdapter::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    @Override
    public Flux<Proveedor> findAll() {
        return repo.findAll().map(R2dbcProveedorAdapter::toDomain);
    }

    @Override
    public Mono<Void> deleteByUid(UUID uid) {
        return repo.deleteByUid(uid);
    }
}
