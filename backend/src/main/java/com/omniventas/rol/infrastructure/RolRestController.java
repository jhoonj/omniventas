package com.omniventas.rol.infrastructure;

import com.omniventas.rol.domain.Rol;
import com.omniventas.rol.domain.RolRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/roles")
public class RolRestController {

    private final RolRepository repo;

    public RolRestController(RolRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<Rol> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Rol> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    // POST upsert: si viene id -> actualiza; si no -> crea
    @PostMapping
    public Mono<Rol> save(@RequestBody Rol rol) {
        return repo.save(rol);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return repo.deleteById(id);
    }

    // opcional: lookup por nombre
    @GetMapping("/nombre/{nombre}")
    public Mono<Rol> getByNombre(@PathVariable String nombre) {
        return repo.findByNombre(nombre);
    }
}
