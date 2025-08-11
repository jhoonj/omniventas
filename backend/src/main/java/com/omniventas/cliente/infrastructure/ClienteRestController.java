package com.omniventas.cliente.infrastructure;

import com.omniventas.cliente.domain.Cliente;
import com.omniventas.cliente.domain.ClienteRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clientes")
public class ClienteRestController {

    private final ClienteRepository clienteRepo;

    public ClienteRestController(ClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    @GetMapping
    public Flux<Cliente> getAll() {
        return clienteRepo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Cliente> getById(@PathVariable Long id) {
        return clienteRepo.findById(id);
    }

    @PostMapping
    public Mono<Cliente> create(@RequestBody Cliente cliente) {
        return clienteRepo.save(cliente);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return clienteRepo.deleteById(id);
    }
}