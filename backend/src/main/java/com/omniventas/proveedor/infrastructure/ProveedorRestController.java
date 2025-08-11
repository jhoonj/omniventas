package com.omniventas.proveedor.infrastructure;

import com.omniventas.proveedor.domain.Proveedor;
import com.omniventas.proveedor.domain.ProveedorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorRestController {

    private final ProveedorRepository proveedores;

    public ProveedorRestController(ProveedorRepository proveedores) {
        this.proveedores = proveedores;
    }

    @GetMapping
    public Flux<Proveedor> listar() {
        return proveedores.findAll();
    }

    @GetMapping("/{uid}")
    public Mono<Proveedor> obtener(@PathVariable UUID uid) {
        return proveedores.findByUid(uid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Proveedor> crear(@RequestBody Proveedor req) {
        // si trae email, valida duplicado (CITEXT)
        Mono<Void> validar = (req.email() == null || req.email().isBlank())
                ? Mono.empty()
                : proveedores.existsByEmail(req.email())
                .flatMap(exists -> exists
                        ? Mono.error(new IllegalArgumentException("El email de proveedor ya está registrado"))
                        : Mono.empty());

        return validar.then(
                proveedores.save(
                        new Proveedor(
                                null, null,
                                req.nombre(),
                                req.email(),
                                req.telefono(),
                                req.direccion(),
                                null, null
                        )
                )
        );
    }

    @PatchMapping("/{uid}")
    public Mono<Proveedor> actualizar(@PathVariable UUID uid, @RequestBody Map<String, Object> patch) {
        return proveedores.findByUid(uid)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Proveedor no encontrado")))
                .flatMap(actual -> {
                    String nombre   = (String) patch.getOrDefault("nombre", actual.nombre());
                    String email    = (String) patch.getOrDefault("email", actual.email());
                    String telefono = (String) patch.getOrDefault("telefono", actual.telefono());
                    String direccion= (String) patch.getOrDefault("direccion", actual.direccion());

                    Mono<Void> validar = Mono.empty();
                    if (email != null && !email.equalsIgnoreCase(actual.email())) {
                        validar = proveedores.existsByEmail(email)
                                .flatMap(ex -> ex ? Mono.error(new IllegalArgumentException("El email de proveedor ya está registrado")) : Mono.empty());
                    }

                    Proveedor actualizado = new Proveedor(
                            actual.id(), actual.uid(),
                            nombre, email, telefono, direccion,
                            actual.createdAt(), actual.updatedAt()
                    );

                    return validar.then(proveedores.save(actualizado));
                });
    }

    @DeleteMapping("/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable UUID uid) {
        return proveedores.deleteByUid(uid);
    }
}
