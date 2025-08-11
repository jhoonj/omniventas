package com.omniventas.detalle.infrastructure;

import com.omniventas.detalle.domain.DetallePedido;
import com.omniventas.detalle.domain.DetallePedidoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcDetalleAdapter implements DetallePedidoRepository {

    private final R2dbcDetalleRepository repo;

    public R2dbcDetalleAdapter(R2dbcDetalleRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<DetallePedido> save(DetallePedido d) {
        var e = new R2dbcDetalleEntity();
        e.setId(d.id());
        e.setPedidoId(d.pedidoId());
        e.setProductoId(d.productoId());
        e.setCantidad(d.cantidad());
        e.setPrecioUnitario(d.precioUnitario());
        return repo.save(e).map(ent -> new DetallePedido(
            ent.getId(), ent.getPedidoId(), ent.getProductoId(), ent.getCantidad(), ent.getPrecioUnitario()));
    }

    @Override
    public Mono<DetallePedido> findById(Long id) {
        return repo.findById(id).map(ent -> new DetallePedido(
            ent.getId(), ent.getPedidoId(), ent.getProductoId(), ent.getCantidad(), ent.getPrecioUnitario()));
    }

    @Override
    public Flux<DetallePedido> findAll() {
        return repo.findAll().map(ent -> new DetallePedido(
            ent.getId(), ent.getPedidoId(), ent.getProductoId(), ent.getCantidad(), ent.getPrecioUnitario()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repo.deleteById(id);
    }
}