package com.optica.envio.infrastructure;

import com.optica.envio.domain.Envio;
import com.optica.envio.domain.EnvioRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/envios")
public class EnvioRestController {

    private final EnvioRepository repo;

    public EnvioRestController(EnvioRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<Envio> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Envio> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @PostMapping
    public Mono<Envio> create(@RequestBody Envio envio) {
        return repo.save(envio);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return repo.deleteById(id);
    }
}