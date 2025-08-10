package com.optica.usuario.infrastructure;

import com.optica.usuario.domain.Usuario;
import com.optica.usuario.domain.UsuarioRepository;
import com.optica.usuario.domain.UsuarioResponseDTO;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRestController {

    private final UsuarioRepository repo;

    public UsuarioRestController(UsuarioRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<Usuario> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Usuario> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @PostMapping
    public Mono<UsuarioResponseDTO> create(@RequestBody Usuario usuario) {
        return repo.save(usuario)
                .map(u -> new UsuarioResponseDTO(
                        u.id(),
                        u.nombre(),
                        u.email(),
                        u.rol_id()
                ));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return repo.deleteById(id);
    }
}