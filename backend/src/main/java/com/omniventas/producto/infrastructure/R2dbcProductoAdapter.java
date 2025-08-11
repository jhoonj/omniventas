package com.omniventas.producto.infrastructure;

import com.omniventas.producto.domain.Producto;
import com.omniventas.producto.domain.ProductoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcProductoAdapter implements ProductoRepository {

    private final R2dbcProductoRepository repo;

    public R2dbcProductoAdapter(R2dbcProductoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        var e = new R2dbcProductoEntity();
        e.setId(producto.id());
        e.setNombre(producto.nombre());
        e.setDescripcion(producto.descripcion());
        e.setTipo(producto.tipo());
        e.setPrecio(producto.precio());
        e.setStock(producto.stock());
        e.setProveedorId(producto.proveedorId());
        return repo.save(e).map(p -> new Producto(
            p.getId(), p.getNombre(), p.getDescripcion(), p.getTipo(),
            p.getPrecio(), p.getStock(), p.getProveedorId()));
    }

    @Override
    public Mono<Producto> findById(Long id) {
        return repo.findById(id).map(p -> new Producto(
            p.getId(), p.getNombre(), p.getDescripcion(), p.getTipo(),
            p.getPrecio(), p.getStock(), p.getProveedorId()));
    }

    @Override
    public Flux<Producto> findAll() {
        return repo.findAll().map(p -> new Producto(
            p.getId(), p.getNombre(), p.getDescripcion(), p.getTipo(),
            p.getPrecio(), p.getStock(), p.getProveedorId()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repo.deleteById(id);
    }
}