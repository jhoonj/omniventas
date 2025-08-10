package com.optica.pedido.infrastructure;

import com.optica.pedido.domain.Pedido;
import com.optica.pedido.domain.PedidoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcPedidoAdapter implements PedidoRepository {

    private final R2dbcPedidoRepository repo;

    public R2dbcPedidoAdapter(R2dbcPedidoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Pedido> save(Pedido pedido) {
        var e = new R2dbcPedidoEntity();
        e.setId(pedido.id());
        e.setClienteId(pedido.clienteId());
        e.setFechaPedido(pedido.fechaPedido());
        e.setEstado(pedido.estado());
        e.setTotal(pedido.total());
        e.setFormulaId(pedido.formulaId());
        return repo.save(e).map(p -> new Pedido(
            p.getId(), p.getClienteId(), p.getFechaPedido(), p.getEstado(), p.getTotal(), p.getFormulaId()));
    }

    @Override
    public Mono<Pedido> findById(Long id) {
        return repo.findById(id).map(p -> new Pedido(
            p.getId(), p.getClienteId(), p.getFechaPedido(), p.getEstado(), p.getTotal(), p.getFormulaId()));
    }

    @Override
    public Flux<Pedido> findAll() {
        return repo.findAll().map(p -> new Pedido(
            p.getId(), p.getClienteId(), p.getFechaPedido(), p.getEstado(), p.getTotal(), p.getFormulaId()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repo.deleteById(id);
    }
}