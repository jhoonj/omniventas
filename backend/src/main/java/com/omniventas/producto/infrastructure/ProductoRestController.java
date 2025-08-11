package com.omniventas.producto.infrastructure;

import com.omniventas.producto.domain.MovimientoInventarioReq;
import com.omniventas.producto.domain.Producto;
import com.omniventas.producto.domain.ProductoRepository;
import com.omniventas.producto.domain.StockRes;
import com.omniventas.shared.api.ApiEnvelope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoRepository productos;

    public ProductoRestController(ProductoRepository productos) {
        this.productos = productos;
    }

    // -------- Listar --------
    @GetMapping
    public Flux<ProductoRes> listar() {
        return productos.findAll().map(ProductoRes::of);
    }

    // -------- Obtener por UID --------
    @GetMapping("/{uid}")
    public Mono<ProductoRes> obtener(@PathVariable UUID uid) {
        return productos.findByUid(uid).map(ProductoRes::of);
    }

    // -------- Crear (usa insert) --------
// ProductoRestController.java
    @PostMapping
    public Mono<ResponseEntity<ApiEnvelope<ProductoRes>>> save(@RequestBody Mono<com.fasterxml.jackson.databind.JsonNode> body) {
        return body.flatMap(json -> {
            // Detecta si es create o update por presencia de uid
            final java.util.UUID uid = json.hasNonNull("uid") ? java.util.UUID.fromString(json.get("uid").asText()) : null;
            final boolean isCreate = (uid == null);

            // Campos opcionales (solo se actualizan si vienen)
            final String nombre      = json.has("nombre")      ? (json.get("nombre").isNull() ? null : json.get("nombre").asText()) : null;
            final String descripcion = json.has("descripcion") ? (json.get("descripcion").isNull() ? null : json.get("descripcion").asText()) : null;
            final String tipo        = json.has("tipo")        ? (json.get("tipo").isNull() ? null : json.get("tipo").asText()) : null;

            final java.math.BigDecimal precio =
                    json.has("precio") && !json.get("precio").isNull()
                            ? new java.math.BigDecimal(json.get("precio").asText())
                            : null;

            // proveedorUid: necesitamos saber si vino o no (para no tocar si se omite)
            final boolean proveedorUidPresent = json.has("proveedorUid");
            final java.util.UUID proveedorUid =
                    proveedorUidPresent && !json.get("proveedorUid").isNull()
                            ? java.util.UUID.fromString(json.get("proveedorUid").asText())
                            : null;

            Mono<com.omniventas.producto.domain.Producto> op =
                    isCreate
                            // CREATE: no mandamos uid (lo genera Postgres con uuid_generate_v4)
                            ? productos.insert(
                            nombre,              // requerido (valídalo si quieres)
                            descripcion,
                            tipo,
                            precio,
                            proveedorUid         // puede ser null
                    )
                            // UPDATE parcial por uid: solo columnas presentes se tocan
                            : productos.updateByUid(
                            uid,
                            nombre,              // si es null y no vino => no cambia
                            descripcion,
                            tipo,
                            precio,
                            proveedorUid,        // si proveedorUidPresent && null => limpia proveedor_id
                            proveedorUidPresent
                    );

            return op.map(p -> {
                var res = ProductoRes.of(p);   // 👈 sin el segundo parámetro
                return ResponseEntity
                        .status(isCreate ? HttpStatus.CREATED : HttpStatus.OK)
                        .body(ApiEnvelope.ok(res));
            })

                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")));
        });
    }

    // -------- Actualizar parcial (usa updateByUid) --------
    // Acepta un PATCH genérico tipo {"nombre": "...", "precio": 123, "proveedorUid": "...."}
    @PatchMapping("/{uid}")
    public Mono<ProductoRes> actualizar(@PathVariable UUID uid,
                                        @RequestBody Map<String, Object> patch) {
        // Extrae campos presentes; si no vienen, quedan null y NO se actualizan.
        String nombre = asString(patch.get("nombre"));
        String descripcion = asString(patch.get("descripcion"));
        String tipo = asString(patch.get("tipo"));
        BigDecimal precio = asBigDecimal(patch.get("precio"));

        boolean proveedorUidPresent = patch.containsKey("proveedorUid");
        UUID proveedorUid = null;
        if (proveedorUidPresent) {
            Object raw = patch.get("proveedorUid");
            if (raw != null && StringUtils.hasText(raw.toString())
                    && !"null".equalsIgnoreCase(raw.toString())) {
                proveedorUid = UUID.fromString(raw.toString());
            } // si viene vacío o "null", lo dejaremos como null ⇒ set NULL en DB
        }

        validarUpdate(nombre, descripcion, tipo, precio);

        return productos.updateByUid(
                        uid,
                        nombre,
                        descripcion,
                        tipo,
                        precio,
                        proveedorUid,
                        proveedorUidPresent
                )
                .map(ProductoRes::of);
    }

    // -------- Eliminar --------
    @DeleteMapping("/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable UUID uid) {
        return productos.deleteByUid(uid);
    }

    // -------- Stock --------
    @GetMapping("/{uid}/stock")
    public Mono<StockRes> stock(@PathVariable UUID uid) {
        return productos.stock(uid);
    }

    // -------- Movimiento inventario --------
    @PostMapping("/{uid}/movimientos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> movimiento(@PathVariable UUID uid,
                                 @RequestBody MovimientoInventarioReq req) {
        return productos.registrarMovimiento(uid, req);
    }

    // ================== Helpers ==================

    private static String asString(Object o) {
        if (o == null) return null;
        var s = o.toString();
        return StringUtils.hasText(s) ? s : null;
    }

    private static BigDecimal asBigDecimal(Object o) {
        if (o == null) return null;
        try {
            return (o instanceof BigDecimal bd) ? bd : new BigDecimal(o.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("precio inválido");
        }
    }

    private static void validarCreate(ProductoCreateReq req) {
        if (!StringUtils.hasText(req.nombre()) || req.nombre().trim().length() < 2) {
            throw new IllegalArgumentException("nombre requerido (mín 2 caracteres)");
        }
        if (req.precio() == null || req.precio().signum() < 0) {
            throw new IllegalArgumentException("precio requerido y ≥ 0");
        }
        if (req.precio().scale() > 2) {
            throw new IllegalArgumentException("precio admite máximo 2 decimales");
        }
        if (req.tipo() != null && req.tipo().length() > 10) {
            throw new IllegalArgumentException("tipo supera longitud máxima");
        }
    }

    private static void validarUpdate(String nombre, String descripcion, String tipo, BigDecimal precio) {
        if (nombre != null && nombre.trim().length() < 2) {
            throw new IllegalArgumentException("nombre (mín 2 caracteres)");
        }
        if (precio != null) {
            if (precio.signum() < 0) throw new IllegalArgumentException("precio debe ser ≥ 0");
            if (precio.scale() > 2) throw new IllegalArgumentException("precio admite máximo 2 decimales");
        }
        if (tipo != null && tipo.length() > 10) {
            throw new IllegalArgumentException("tipo supera longitud máxima");
        }
    }

    // ================== DTOs ==================

    public record ProductoCreateReq(
            String nombre,
            String descripcion,
            String tipo,
            BigDecimal precio,
            UUID proveedorUid
    ) {}

    // Respuesta. Ajusta a tu clase real si ya la tienes en otro paquete.
    public record ProductoRes(
            UUID uid,
            String nombre,
            String descripcion,
            String tipo,
            BigDecimal precio,
            Long proveedorId,
            Instant updatedAt,
            Integer stock
    ) {
        public static ProductoRes of(Producto p) {
            return new ProductoRes(
                    p.uid(),
                    p.nombre(),
                    p.descripcion(),
                    p.tipo(),
                    p.precio(),
                    p.proveedorId(),
                    p.updatedAt(),
                    p.stock()
            );
        }
    }
}
