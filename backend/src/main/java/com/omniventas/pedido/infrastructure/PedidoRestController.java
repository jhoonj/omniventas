package com.omniventas.pedido.infrastructure;

import com.omniventas.pedido.domain.Pedido;
import com.omniventas.pedido.domain.PedidoRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/pedidos")
public class PedidoRestController {

    private final PedidoRepository repo;

    public PedidoRestController(PedidoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<Pedido> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Pedido> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @PostMapping
    public Mono<Pedido> create(@RequestBody Pedido pedido) {
        return repo.save(pedido);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return repo.deleteById(id);
    }
}