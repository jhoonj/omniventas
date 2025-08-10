package com.optica.envio.infrastructure;

import com.optica.envio.domain.Envio;
import com.optica.envio.domain.EnvioRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcEnvioAdapter implements EnvioRepository {

    private final R2dbcEnvioRepository repo;

    public R2dbcEnvioAdapter(R2dbcEnvioRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Envio> save(Envio e) {
        var entity = new R2dbcEnvioEntity();
        entity.setId(e.id());
        entity.setPedidoId(e.pedidoId());
        entity.setDireccionEnvio(e.direccionEnvio());
        entity.setEmpresaEnvio(e.empresaEnvio());
        entity.setNumeroGuia(e.numeroGuia());
        entity.setEstadoEnvio(e.estadoEnvio());
        entity.setFechaEnvio(e.fechaEnvio());
        entity.setFechaEntregaEstimada(e.fechaEntregaEstimada());
        return repo.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<Envio> findById(Long id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Envio> findAll() {
        return repo.findAll().map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repo.deleteById(id);
    }

    private Envio toDomain(R2dbcEnvioEntity e) {
        return new Envio(
            e.getId(), e.getPedidoId(), e.getDireccionEnvio(), e.getEmpresaEnvio(),
            e.getNumeroGuia(), e.getEstadoEnvio(), e.getFechaEnvio(), e.getFechaEntregaEstimada());
    }
}