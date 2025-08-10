package com.optica.pago.infrastructure;

import com.optica.pago.domain.Pago;
import com.optica.pago.domain.PagoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcPagoAdapter implements PagoRepository {

    private final R2dbcPagoRepository repo;

    public R2dbcPagoAdapter(R2dbcPagoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Pago> save(Pago p) {
        var e = new R2dbcPagoEntity();
        e.setId(p.id());
        e.setPedidoId(p.pedidoId());
        e.setFechaPago(p.fechaPago());
        e.setMetodoPago(p.metodoPago());
        e.setMonto(p.monto());
        e.setEstado(p.estado());
        return repo.save(e).map(ent -> new Pago(
            ent.getId(), ent.getPedidoId(), ent.getFechaPago(), ent.getMetodoPago(), ent.getMonto(), ent.getEstado()));
    }

    @Override
    public Mono<Pago> findById(Long id) {
        return repo.findById(id).map(ent -> new Pago(
            ent.getId(), ent.getPedidoId(), ent.getFechaPago(), ent.getMetodoPago(), ent.getMonto(), ent.getEstado()));
    }

    @Override
    public Flux<Pago> findAll() {
        return repo.findAll().map(ent -> new Pago(
            ent.getId(), ent.getPedidoId(), ent.getFechaPago(), ent.getMetodoPago(), ent.getMonto(), ent.getEstado()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repo.deleteById(id);
    }
}