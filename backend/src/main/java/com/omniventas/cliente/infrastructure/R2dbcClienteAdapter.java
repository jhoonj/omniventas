package com.omniventas.cliente.infrastructure;

import com.omniventas.cliente.domain.Cliente;
import com.omniventas.cliente.domain.ClienteRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public class R2dbcClienteAdapter implements ClienteRepository {

    private final R2dbcClienteRepository repo;

    public R2dbcClienteAdapter(R2dbcClienteRepository repo) {
        this.repo = repo;
    }

    private static Cliente toDomain(R2dbcClienteEntity e) {
        if (e == null) return null;
        return new Cliente(
                e.getId(),
                e.getUid(),
                e.getNombre(),
                e.getEmail(),
                e.getTelefono(),
                e.getDireccion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private static R2dbcClienteEntity toEntity(Cliente c) {
        R2dbcClienteEntity e = new R2dbcClienteEntity();
        e.setId(c.id());
        e.setUid(c.uid() != null ? c.uid() : UUID.randomUUID()); // por si viene null, DB también tiene DEFAULT
        e.setNombre(c.nombre());
        e.setEmail(c.email());
        e.setTelefono(c.telefono());
        e.setDireccion(c.direccion());
        e.setCreatedAt(c.createdAt());
        e.setUpdatedAt(c.updatedAt());
        return e;
    }

    @Override
    public Mono<Cliente> save(Cliente cliente) {
        R2dbcClienteEntity entity = toEntity(cliente);
        // Si es creación y no trae timestamps, inicializa
        if (entity.getId() == null) {
            if (entity.getUid() == null) entity.setUid(UUID.randomUUID());
            if (entity.getCreatedAt() == null) entity.setCreatedAt(Instant.now());
            entity.setUpdatedAt(Instant.now());
        } else {
            entity.setUpdatedAt(Instant.now());
        }
        return repo.save(entity).map(R2dbcClienteAdapter::toDomain);
    }

    @Override
    public Mono<Cliente> findByUid(UUID uid) {
        return repo.findByUid(uid).map(R2dbcClienteAdapter::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    @Override
    public Flux<Cliente> findAll() {
        return repo.findAll().map(R2dbcClienteAdapter::toDomain);
    }

    @Override
    public Mono<Void> deleteByUid(UUID uid) {
        return repo.deleteByUid(uid);
    }
}
