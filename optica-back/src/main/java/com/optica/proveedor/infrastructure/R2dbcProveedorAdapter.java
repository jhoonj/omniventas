package com.optica.proveedor.infrastructure;

import com.optica.proveedor.domain.Proveedor;
import com.optica.proveedor.domain.ProveedorRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcProveedorAdapter implements ProveedorRepository {

    private final R2dbcProveedorRepository repo;

    public R2dbcProveedorAdapter(R2dbcProveedorRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Proveedor> save(Proveedor p) {
        var e = new R2dbcProveedorEntity();
        e.setId(p.id());
        e.setNombre(p.nombre());
        e.setEmail(p.email());
        e.setTelefono(p.telefono());
        e.setDireccion(p.direccion());
        return repo.save(e).map(r -> new Proveedor(r.getId(), r.getNombre(), r.getEmail(), r.getTelefono(), r.getDireccion()));
    }

    @Override
    public Mono<Proveedor> findById(Long id) {
        return repo.findById(id).map(r -> new Proveedor(r.getId(), r.getNombre(), r.getEmail(), r.getTelefono(), r.getDireccion()));
    }

    @Override
    public Flux<Proveedor> findAll() {
        return repo.findAll().map(r -> new Proveedor(r.getId(), r.getNombre(), r.getEmail(), r.getTelefono(), r.getDireccion()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repo.deleteById(id);
    }
}