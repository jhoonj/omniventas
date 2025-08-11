package com.omniventas.formula.infrastructure;

import com.omniventas.formula.domain.FormulaMedica;
import com.omniventas.formula.domain.FormulaRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/formulas")
public class FormulaRestController {

    private final FormulaRepository repo;

    public FormulaRestController(FormulaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<FormulaMedica> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<FormulaMedica> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @PostMapping
    public Mono<FormulaMedica> create(@RequestBody FormulaMedica formula) {
        return repo.save(formula);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return repo.deleteById(id);
    }
}