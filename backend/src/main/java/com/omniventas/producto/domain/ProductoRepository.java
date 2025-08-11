package com.omniventas.producto.domain;

import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductoRepository {

    Flux<Producto> findAll();

    Mono<Producto> findByUid(UUID uid);

    Mono<Producto> insert(
            String nombre,
            @Nullable String descripcion,
            @Nullable String tipo,
            @Nullable BigDecimal precio,
            @Nullable UUID proveedorUid
    );

    /**
     * Actualiza por UID. Solo actualiza los campos no-nulos.
     * Si proveedorUidPresent = true y proveedorUid = null → pone proveedor_id = NULL.
     */
    Mono<Producto> updateByUid(
            UUID uid,
            @Nullable String nombre,
            @Nullable String descripcion,
            @Nullable String tipo,
            @Nullable BigDecimal precio,
            @Nullable UUID proveedorUid,
            boolean proveedorUidPresent
    );

    Mono<Void> deleteByUid(UUID uid);

    Mono<Void> registrarMovimiento(UUID productoUid, MovimientoInventarioReq req);

    Mono<StockRes> stock(UUID productoUid);
}
