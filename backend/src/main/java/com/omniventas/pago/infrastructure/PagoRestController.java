package com.omniventas.pago.infrastructure;

import com.omniventas.pago.domain.Pago;
import com.omniventas.pago.domain.PagoRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/pagos")
public class PagoRestController {

    private final PagoRepository repo;

    public PagoRestController(PagoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<Pago> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Pago> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @PostMapping
    public Mono<Pago> create(@RequestBody Pago pago) {
        return repo.save(pago);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return repo.deleteById(id);
    }
}