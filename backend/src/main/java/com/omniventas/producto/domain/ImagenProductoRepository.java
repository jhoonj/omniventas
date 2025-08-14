package com.omniventas.producto.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ImagenProductoRepository {

    Mono<ImagenMeta> findMeta(UUID productoUid, UUID imagenUid);

    Mono<Long> resolveProductoId(UUID productoUid);

    Flux<ImagenProducto> listar(UUID productoUid);

    // Usa el ID interno (Long) ya resuelto para escribir
    Mono<ImagenProducto> insert(Long productoId, ImagenProducto meta);

    // Operaciones “por producto” que tocan muchas filas: mejor por ID interno
    Mono<Void> marcarPrincipal(Long productoId, UUID imagenUid);
    Mono<Void> limpiarPrincipalesExcepto(Long productoId, UUID imagenUid);

    // Consultas que exponen API: por UID público
    Mono<ImagenProducto> findByUids(UUID productoUid, UUID imagenUid);

    // Borrado por UID público (más natural en la API)
    Mono<Void> eliminar(UUID productoUid, UUID imagenUid);
}
