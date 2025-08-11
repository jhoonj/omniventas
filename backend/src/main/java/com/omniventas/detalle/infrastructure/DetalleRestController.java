package com.omniventas.detalle.infrastructure;

import com.omniventas.detalle.domain.DetallePedido;
import com.omniventas.detalle.domain.DetallePedidoRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/detalles")
public class DetalleRestController {

    private final DetallePedidoRepository repo;

    public DetalleRestController(DetallePedidoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<DetallePedido> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<DetallePedido> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @PostMapping
    public Mono<DetallePedido> create(@RequestBody DetallePedido detalle) {
        return repo.save(detalle);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return repo.deleteById(id);
    }
}