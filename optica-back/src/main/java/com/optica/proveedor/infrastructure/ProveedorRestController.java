package com.optica.proveedor.infrastructure;

import com.optica.proveedor.domain.Proveedor;
import com.optica.proveedor.domain.ProveedorRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/proveedores")
public class ProveedorRestController {

    private final ProveedorRepository repo;

    public ProveedorRestController(ProveedorRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<Proveedor> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Proveedor> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @PostMapping
    public Mono<Proveedor> create(@RequestBody Proveedor proveedor) {
        return repo.save(proveedor);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return repo.deleteById(id);
    }
}