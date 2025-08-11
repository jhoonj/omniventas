package com.omniventas.producto.infrastructure;

import com.omniventas.producto.domain.Producto;
import com.omniventas.producto.domain.ProductoRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/productos")
public class ProductoRestController {

    private final ProductoRepository repo;

    public ProductoRestController(ProductoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<Producto> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Producto> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @PostMapping
    public Mono<Producto> create(@RequestBody Producto producto) {
        return repo.save(producto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return repo.deleteById(id);
    }
}