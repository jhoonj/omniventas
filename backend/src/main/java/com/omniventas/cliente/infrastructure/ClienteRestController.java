package com.omniventas.cliente.infrastructure;

import com.omniventas.cliente.domain.Cliente;
import com.omniventas.cliente.domain.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    private final ClienteRepository clientes;

    public ClienteRestController(ClienteRepository clientes) {
        this.clientes = clientes;
    }

    // Listar
    @GetMapping
    public Flux<Cliente> listar() {
        return clientes.findAll();
    }

    // Obtener por UID
    @GetMapping("/{uid}")
    public Mono<Cliente> obtener(@PathVariable UUID uid) {
        return clientes.findByUid(uid);
    }

    // Crear
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Cliente> crear(@RequestBody Cliente req) {
        // Validación simple de email único (citext en DB)
        return clientes.existsByEmail(req.email())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new IllegalArgumentException("El email ya está registrado"));
                    }
                    return clientes.save(
                            new Cliente(
                                    null, // id
                                    null, // uid -> se genera
                                    req.nombre(),
                                    req.email(),
                                    req.telefono(),
                                    req.direccion(),
                                    null,
                                    null
                            )
                    );
                });
    }

    // Actualizar parcial (PATCH)
    @PatchMapping("/{uid}")
    public Mono<Cliente> actualizarParcial(@PathVariable UUID uid, @RequestBody Map<String, Object> patch) {
        return clientes.findByUid(uid)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente no encontrado")))
                .flatMap(actual -> {
                    String nombre = (String) patch.getOrDefault("nombre", actual.nombre());
                    String email = (String) patch.getOrDefault("email", actual.email());
                    String telefono = (String) patch.getOrDefault("telefono", actual.telefono());
                    String direccion = (String) patch.getOrDefault("direccion", actual.direccion());

                    // Si cambia email, valida duplicado
                    Mono<Void> validar = email.equalsIgnoreCase(actual.email())
                            ? Mono.empty()
                            : clientes.existsByEmail(email).flatMap(ex -> ex ? Mono.error(new IllegalArgumentException("El email ya está registrado")) : Mono.empty());

                    return validar.then(
                            clientes.save(
                                    new Cliente(
                                            actual.id(),
                                            actual.uid(),
                                            nombre,
                                            email,
                                            telefono,
                                            direccion,
                                            actual.createdAt(),
                                            actual.updatedAt()
                                    )
                            )
                    );
                });
    }

    // Eliminar
    @DeleteMapping("/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable UUID uid) {
        return clientes.deleteByUid(uid);
    }
}
