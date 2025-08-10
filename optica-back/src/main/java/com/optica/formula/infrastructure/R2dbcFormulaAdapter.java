package com.optica.formula.infrastructure;

import com.optica.formula.domain.FormulaMedica;
import com.optica.formula.domain.FormulaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcFormulaAdapter implements FormulaRepository {

    private final R2dbcFormulaRepository repo;

    public R2dbcFormulaAdapter(R2dbcFormulaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<FormulaMedica> save(FormulaMedica f) {
        var e = new R2dbcFormulaEntity();
        e.setId(f.id());
        e.setClienteId(f.clienteId());
        e.setFechaEmision(f.fechaEmision());
        e.setOdEsfera(f.odEsfera());
        e.setOdCilindro(f.odCilindro());
        e.setOdEje(f.odEje());
        e.setOiEsfera(f.oiEsfera());
        e.setOiCilindro(f.oiCilindro());
        e.setOiEje(f.oiEje());
        e.setDistanciaInterpupilar(f.distanciaInterpupilar());
        e.setObservaciones(f.observaciones());
        return repo.save(e).map(entity -> new FormulaMedica(
            entity.getId(), entity.getClienteId(), entity.getFechaEmision(),
            entity.getOdEsfera(), entity.getOdCilindro(), entity.getOdEje(),
            entity.getOiEsfera(), entity.getOiCilindro(), entity.getOiEje(),
            entity.getDistanciaInterpupilar(), entity.getObservaciones()));
    }

    @Override
    public Mono<FormulaMedica> findById(Long id) {
        return repo.findById(id).map(entity -> new FormulaMedica(
            entity.getId(), entity.getClienteId(), entity.getFechaEmision(),
            entity.getOdEsfera(), entity.getOdCilindro(), entity.getOdEje(),
            entity.getOiEsfera(), entity.getOiCilindro(), entity.getOiEje(),
            entity.getDistanciaInterpupilar(), entity.getObservaciones()));
    }

    @Override
    public Flux<FormulaMedica> findAll() {
        return repo.findAll().map(entity -> new FormulaMedica(
            entity.getId(), entity.getClienteId(), entity.getFechaEmision(),
            entity.getOdEsfera(), entity.getOdCilindro(), entity.getOdEje(),
            entity.getOiEsfera(), entity.getOiCilindro(), entity.getOiEje(),
            entity.getDistanciaInterpupilar(), entity.getObservaciones()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repo.deleteById(id);
    }
}