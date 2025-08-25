package com.omniventas.producto.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.omniventas.producto.domain.MovimientoInventarioReq;
import com.omniventas.producto.domain.Producto;
import com.omniventas.producto.domain.ProductoRepository;
import com.omniventas.producto.infrastructure.dto.ProductoRes;
import com.omniventas.producto.domain.StockRes;
import com.omniventas.shared.api.ApiEnvelope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
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

    // -------- Crear/Actualizar (POST save) --------
    @PostMapping
    public Mono<ResponseEntity<ApiEnvelope<ProductoRes>>> save(@RequestBody Mono<JsonNode> body) {
        System.out.println(body);
        return body.flatMap(json -> {
            final UUID uid = json.hasNonNull("uid") ? UUID.fromString(json.get("uid").asText()) : null;
            final boolean isCreate = (uid == null);

            final String nombre      = optText(json, "nombre");
            final String descripcion = optText(json, "descripcion");
            final String tipo        = optText(json, "tipo");
            final BigDecimal precio  = optBigDecimal(json, "precio");

            final boolean proveedorUidPresent = json.has("proveedorUid");
            final UUID proveedorUid =
                    proveedorUidPresent && json.hasNonNull("proveedorUid")
                            ? UUID.fromString(json.get("proveedorUid").asText())
                            : null;

            Mono<Producto> op = isCreate
                    ? productos.insert(nombre, descripcion, tipo, precio, proveedorUid)
                    : productos.updateByUid(uid, nombre, descripcion, tipo, precio, proveedorUid, proveedorUidPresent);

            return op.map(p -> {
                        var res = ProductoRes.of(p);
                        var env = ApiEnvelope.ok(res);

                        if (isCreate) {
                            // Header Location -> /omniventas/api/productos/{uid}
                            URI location = URI.create("/omniventas/api/productos/" + p.uid().toString());
                            return ResponseEntity.status(HttpStatus.CREATED)
                                    .header(HttpHeaders.LOCATION, location.toString())
                                    .body(env);
                        }
                        return ResponseEntity.ok(env);
                    })
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")));
        });
    }

    // -------- Actualizar parcial (PATCH) --------
    @PatchMapping("/{uid}")
    public Mono<ResponseEntity<ApiEnvelope<ProductoRes>>> actualizar(@PathVariable UUID uid,
                                                                     @RequestBody Map<String, Object> patch) {
        String nombre = asString(patch.get("nombre"));
        String descripcion = asString(patch.get("descripcion"));
        String tipo = asString(patch.get("tipo"));
        BigDecimal precio = asBigDecimal(patch.get("precio"));

        boolean proveedorUidPresent = patch.containsKey("proveedorUid");
        UUID proveedorUid = null;
        if (proveedorUidPresent) {
            Object raw = patch.get("proveedorUid");
            if (raw != null && StringUtils.hasText(raw.toString()) && !"null".equalsIgnoreCase(raw.toString())) {
                proveedorUid = UUID.fromString(raw.toString());
            }
        }

        validarUpdate(nombre, descripcion, tipo, precio);

        return productos.updateByUid(uid, nombre, descripcion, tipo, precio, proveedorUid, proveedorUidPresent)
                .map(ProductoRes::of)
                .map(ApiEnvelope::ok)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")));
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

    // -------- Movimientos de inventario --------
    @PostMapping("/{uid}/movimientos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> movimiento(@PathVariable UUID uid,
                                 @RequestBody MovimientoInventarioReq req) {
        return productos.registrarMovimiento(uid, req);
    }

    // ================== Helpers ==================

    private static String optText(JsonNode json, String key) {
        return json.has(key) && !json.get(key).isNull() ? json.get(key).asText() : null;
    }

    private static BigDecimal optBigDecimal(JsonNode json, String key) {
        if (!(json.has(key) && !json.get(key).isNull())) return null;
        try { return new BigDecimal(json.get(key).asText()); }
        catch (Exception e) { throw new IllegalArgumentException(key + " inválido"); }
    }

    private static String asString(Object o) {
        if (o == null) return null;
        var s = o.toString();
        return StringUtils.hasText(s) ? s : null;
    }

    private static BigDecimal asBigDecimal(Object o) {
        if (o == null) return null;
        try { return (o instanceof BigDecimal bd) ? bd : new BigDecimal(o.toString()); }
        catch (Exception e) { throw new IllegalArgumentException("precio inválido"); }
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
}
