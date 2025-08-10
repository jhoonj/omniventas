package com.optica.cliente.infrastructure;

import com.optica.cliente.domain.Cliente;
import com.optica.cliente.domain.ClienteRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcClienteAdapter implements ClienteRepository {

    private final R2dbcClienteRepository repo;

    public R2dbcClienteAdapter(R2dbcClienteRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Cliente> save(Cliente cliente) {
        var entity = new R2dbcClienteEntity();
        entity.setId(cliente.id());
        entity.setNombre(cliente.nombre());
        entity.setEmail(cliente.email());
        entity.setTelefono(cliente.telefono());
        entity.setDireccion(cliente.direccion());
        return repo.save(entity).map(e -> new Cliente(
            e.getId(), e.getNombre(), e.getEmail(), e.getTelefono(), e.getDireccion()));
    }

    @Override
    public Mono<Cliente> findById(Long id) {
        return repo.findById(id).map(e -> new Cliente(
            e.getId(), e.getNombre(), e.getEmail(), e.getTelefono(), e.getDireccion()));
    }

    @Override
    public Flux<Cliente> findAll() {
        return repo.findAll().map(e -> new Cliente(
            e.getId(), e.getNombre(), e.getEmail(), e.getTelefono(), e.getDireccion()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repo.deleteById(id);
    }
}