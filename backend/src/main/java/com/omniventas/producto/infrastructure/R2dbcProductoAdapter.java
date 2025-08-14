package com.omniventas.producto.infrastructure;

import com.omniventas.producto.domain.InventarioSaldo;
import com.omniventas.producto.domain.MovimientoInventarioReq;
import com.omniventas.producto.domain.Producto;
import com.omniventas.producto.domain.ProductoRepository;
import com.omniventas.producto.domain.StockRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class R2dbcProductoAdapter implements ProductoRepository {

    private static final Logger log = LoggerFactory.getLogger(R2dbcProductoAdapter.class);

    private final DatabaseClient db;

    public R2dbcProductoAdapter(DatabaseClient db) {
        this.db = db;
    }

    private static final String SELECT_BASE = """
  SELECT p.id, p.uid, p.nombre, p.descripcion, p.tipo, p.precio, p.proveedor_id, p.stock,
         (
           SELECT i.url
             FROM public.producto_imagenes i
            WHERE i.producto_id = p.id AND i.principal = TRUE
            ORDER BY i.id DESC
            LIMIT 1
         ) AS imagen_url
    FROM public.productos p
""";


    private static final Set<String> TIPOS_VALIDOS = Set.of(
            "ingreso", "salida", "reserva", "ajuste", "transferencia_in", "transferencia_out"
    );

    private static Producto mapRow(io.r2dbc.spi.Row row) {
        return new Producto(
                row.get("id", Long.class),
                row.get("uid", java.util.UUID.class),
                row.get("nombre", String.class),
                row.get("descripcion", String.class),
                row.get("tipo", String.class),
                row.get("precio", BigDecimal.class),
                row.get("proveedor_id", Long.class),
                null,
                null,
                row.get("imagen_url", String.class),
                row.get("stock", Integer.class)
        );
    }

    private Mono<Long> resolveProveedorId(UUID proveedorUid) {
        return db.sql("SELECT id FROM public.proveedores WHERE uid = :uid")
                .bind("uid", proveedorUid)
                .map((row, meta) -> row.get("id", Long.class))
                .one(); // si no hay filas => Mono.empty()
    }

    private Mono<Long> resolveProductoId(UUID productoUid) {
        return db.sql("SELECT id FROM public.productos WHERE uid = :uid")
                .bind("uid", productoUid)
                .map((row, meta) -> row.get("id", Long.class))
                .one();
    }

    private static int normalizeCantidad(String tipo, int cantidad) {
        if ("ajuste".equals(tipo)) return cantidad;
        return switch (tipo) {
            case "ingreso", "transferencia_in" -> Math.abs(cantidad);
            case "salida", "reserva", "transferencia_out" -> -Math.abs(cantidad);
            default -> cantidad;
        };
    }

    // ====== Repository ======

    @Override
    public Flux<Producto> findAll() {
        return db.sql(SELECT_BASE + " ORDER BY p.nombre ASC")
                .map((row, meta) -> mapRow(row))
                .all();
    }

    @Override
    public Mono<Producto> findByUid(UUID uid) {
        return db.sql(SELECT_BASE + " WHERE p.uid = :uid")
                .bind("uid", uid)
                .map((row, meta) -> mapRow(row))
                .one();
    }

    @Override
    public Mono<Producto> insert(String nombre,
                                 String descripcion,
                                 String tipo,
                                 BigDecimal precio,
                                 UUID proveedorUid) {

        Objects.requireNonNull(nombre, "nombre requerido");

        // Inserta construyendo columnas dinámicamente; NO bindea nulls
        java.util.function.Function<Long, Mono<Producto>> doInsert = (Long provId) -> {
            List<String> cols  = new ArrayList<>();
            List<String> vals  = new ArrayList<>();
            Map<String, Object> binds = new LinkedHashMap<>();

            // uid lo genera Postgres con uuid_generate_v4()
            cols.add("nombre");  vals.add(":nombre");  binds.put("nombre", nombre);

            if (descripcion != null && !descripcion.isBlank()) {
                cols.add("descripcion"); vals.add(":descripcion"); binds.put("descripcion", descripcion);
            }
            if (tipo != null && !tipo.isBlank()) {
                cols.add("tipo"); vals.add(":tipo"); binds.put("tipo", tipo);
            }
            if (precio != null) {
                BigDecimal precioBind = (precio instanceof BigDecimal) ? precio : new BigDecimal(precio.toString());
                cols.add("precio"); vals.add(":precio"); binds.put("precio", precioBind);
            }
            if (provId != null) {
                cols.add("proveedor_id"); vals.add(":proveedor_id"); binds.put("proveedor_id", provId);
            }

            String sql = """
            INSERT INTO public.productos (%s)
            VALUES (%s)
            RETURNING id, uid, nombre, descripcion, tipo, precio, proveedor_id, stock,imagen_url
            """.formatted(String.join(", ", cols), String.join(", ", vals));

            var spec = db.sql(sql);
            for (var e : binds.entrySet()) {
                spec = spec.bind(e.getKey(), e.getValue()); // nunca bindea null
            }
            return spec.map((row, meta) -> mapRow(row)).one(); // ← ya no devuelve empty
        };

        // Ramifica sin producir Monos vacíos
        if (proveedorUid == null) {
            // no se envió proveedor → insert sin proveedor_id
            return doInsert.apply(null);
        } else {
            // se envió proveedor → si existe usamos su id; si no, insert sin proveedor_id
            return resolveProveedorId(proveedorUid)
                    .flatMap(id -> doInsert.apply(id))
                    .switchIfEmpty(doInsert.apply(null));
        }
    }

    /** UPDATE dinámico; si proveedorUidPresent y proveedorUid == null -> proveedor_id = NULL literal (sin bind). */
    @Override
    public Mono<Producto> updateByUid(UUID uid,
                                      String nombre,
                                      String descripcion,
                                      String tipo,
                                      BigDecimal precio,
                                      UUID proveedorUid,
                                      boolean proveedorUidPresent) {

        // Función que arma el UPDATE con el proveedor_id resuelto (o null para limpiar).
        java.util.function.Function<Long, Mono<Producto>> doUpdate = (Long provId) -> {
            List<String> sets = new ArrayList<>();
            Map<String, Object> binds = new LinkedHashMap<>();

            if (nombre != null) {
                sets.add("nombre = :nombre");
                binds.put("nombre", nombre);
            }
            if (descripcion != null) {
                sets.add("descripcion = :descripcion");
                binds.put("descripcion", descripcion);
            }
            if (tipo != null) {
                sets.add("tipo = :tipo");
                binds.put("tipo", tipo);
            }
            if (precio != null) {
                BigDecimal precioBind = (precio instanceof BigDecimal) ? precio : new BigDecimal(precio.toString());
                sets.add("precio = :precio");
                binds.put("precio", precioBind);
            }

            // Sólo tocar proveedor_id si el campo vino en el payload
            if (proveedorUidPresent) {
                if (provId == null) {
                    sets.add("proveedor_id = NULL");                 // limpiar
                } else {
                    sets.add("proveedor_id = :proveedor_id");        // asignar
                    binds.put("proveedor_id", provId);
                }
            }

            if (sets.isEmpty()) {
                // Nada que actualizar → devuelve el registro actual
                return findByUid(uid);
            }

            String sql = """
            UPDATE public.productos
               SET %s
             WHERE uid = :uid
            RETURNING id, uid, nombre, descripcion, tipo, precio, proveedor_id, stock,imagen_url
            """.formatted(String.join(", ", sets));

            var spec = db.sql(sql).bind("uid", uid);
            for (var e : binds.entrySet()) {
                spec = spec.bind(e.getKey(), e.getValue());
            }
            return spec.map((row, meta) -> mapRow(row)).one();
        };

        // ── Ramas sin Monos con null ─────────────────────────────────────────────
        if (!proveedorUidPresent) {
            // No tocar proveedor_id
            return doUpdate.apply(null);
        }
        if (proveedorUid == null) {
            // Limpiar proveedor_id
            return doUpdate.apply(null);
        }
        // Resolver proveedor_id por uid; si no existe, limpiar
        return resolveProveedorId(proveedorUid)
                .flatMap(doUpdate)
                .switchIfEmpty(doUpdate.apply(null));
    }


    @Override
    public Mono<Void> deleteByUid(UUID uid) {
        return db.sql("DELETE FROM public.productos WHERE uid = :uid")
                .bind("uid", uid)
                .fetch()
                .rowsUpdated()
                .then();
    }

    @Override
    public Mono<Void> registrarMovimiento(UUID productoUid, MovimientoInventarioReq req) {
        if (req == null || req.tipo() == null) {
            return Mono.error(new IllegalArgumentException("Tipo de movimiento requerido"));
        }
        final String tipo = req.tipo().toLowerCase().trim();
        if (!TIPOS_VALIDOS.contains(tipo)) {
            return Mono.error(new IllegalArgumentException("Tipo de movimiento inválido: " + req.tipo()));
        }
        if (req.cantidad() == 0) {
            return Mono.error(new IllegalArgumentException("La cantidad no puede ser 0"));
        }

        final int cantidadNorm = normalizeCantidad(tipo, req.cantidad());

        return resolveProductoId(productoUid).flatMap(pid -> {
            // 1) Insert del movimiento
            List<String> cols = new ArrayList<>(List.of("producto_id","tipo","cantidad"));
            List<String> vals = new ArrayList<>(List.of(":pid", ":tipo", ":cantidad"));
            Map<String,Object> binds = new LinkedHashMap<>();
            binds.put("pid", pid);
            binds.put("tipo", tipo);
            binds.put("cantidad", cantidadNorm);

            if (req.referenciaTipo() != null && !req.referenciaTipo().isBlank()) {
                cols.add("referencia_tipo"); vals.add(":rtipo"); binds.put("rtipo", req.referenciaTipo());
            }
            if (req.referenciaId() != null) {
                cols.add("referencia_id"); vals.add(":rid"); binds.put("rid", req.referenciaId());
            }
            if (req.nota() != null && !req.nota().isBlank()) {
                cols.add("nota"); vals.add(":nota"); binds.put("nota", req.nota());
            }
            if (req.almacenId() != null) { // no usas almacenes, pero por si llega null no lo bindemos
                cols.add("almacen_id"); vals.add(":alm"); binds.put("alm", req.almacenId());
            }

            String sql = "INSERT INTO public.inventario_movimientos (" +
                    String.join(", ", cols) + ") VALUES (" + String.join(", ", vals) + ")";

            var spec = db.sql(sql);
            for (var e : binds.entrySet()) spec = spec.bind(e.getKey(), e.getValue());

            // 2) Actualizar el stock del producto
            return spec.fetch().rowsUpdated()
                    .then(updateProductoStock(pid, tipo, cantidadNorm));
        });
    }


    public Mono<Void> clearPrincipal(Long productoId) {
        return db.sql("UPDATE public.producto_imagenes SET principal = FALSE WHERE producto_id = :pid")
                .bind("pid", productoId).fetch().rowsUpdated().then();
    }

    public Mono<Void> insertImagen(Long productoId, String filename, String contentType,
                                   long size, String url, boolean principal, int orden) {
        String sql = """
    INSERT INTO public.producto_imagenes (producto_id, filename, content_type, size_bytes, url, principal, orden)
    VALUES (:pid, :fn, :ct, :sz, :url, :pr, :ord)
  """;
        var spec = db.sql(sql)
                .bind("pid", productoId)
                .bind("fn", filename)
                .bind("ct", contentType)
                .bind("sz", size)
                .bind("url", url)
                .bind("pr", principal)
                .bind("ord", orden);
        return spec.fetch().rowsUpdated().then();
    }

    public Flux<Map<String, Object>> listImagenes(UUID productoUid) {
        return resolveProductoId(productoUid).flatMapMany(pid ->
                db.sql("""
      SELECT uid, url, filename, principal, orden, size_bytes, content_type, created_at
        FROM public.producto_imagenes
       WHERE producto_id = :pid
       ORDER BY principal DESC, orden ASC, id ASC
    """).bind("pid", pid).map((row, meta) -> {
                    // Usar un mapa mutable para forzar V = Object y evitar tipos intersección de Map.of
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("uid", row.get("uid", java.util.UUID.class));
                    m.put("url", row.get("url", String.class));
                    m.put("filename", row.get("filename", String.class));
                    m.put("principal", row.get("principal", Boolean.class));
                    m.put("orden", row.get("orden", Integer.class));
                    m.put("sizeBytes", row.get("size_bytes", Long.class));
                    m.put("contentType", row.get("content_type", String.class));
                    m.put("createdAt", row.get("created_at", java.time.Instant.class));
                    return m;
                }).all()
        );
    }



    /** Actualiza productos.stock sin almacenes.
     *  - ingreso/salida/reserva/transfer_*: suma delta
     *  - ajuste: pone el stock al valor indicado (abs)
     */
    private Mono<Void> updateProductoStock(Long pid, String tipo, int delta) {
        if ("ajuste".equals(tipo)) {
            // Si prefieres que AJUSTE sea un delta y NO un valor absoluto:
            // cambia esta sentencia por la del COALESCE de abajo.
            return db.sql("""
                UPDATE public.productos
                   SET stock = :nuevo
                 WHERE id = :pid
                """)
                    .bind("nuevo", delta)
                    .bind("pid", pid)
                    .fetch().rowsUpdated()
                    .then();
        } else {
            return db.sql("""
                UPDATE public.productos
                   SET stock = COALESCE(stock, 0) + :delta
                 WHERE id = :pid
                """)
                    .bind("delta", delta)
                    .bind("pid", pid)
                    .fetch().rowsUpdated()
                    .then();
        }
    }



    @Override
    public Mono<StockRes> stock(UUID productoUid) {
        return resolveProductoId(productoUid)
                .flatMapMany(pid ->
                        db.sql("""
                    SELECT producto_id, almacen_id, SUM(cantidad)::int AS saldo
                      FROM public.inventario_movimientos
                     WHERE producto_id = :pid
                     GROUP BY producto_id, almacen_id
                    """)
                                .bind("pid", pid)
                                .map((row, meta) -> new AlmacenSaldo(
                                        row.get("almacen_id", UUID.class),
                                        Optional.ofNullable(row.get("saldo", Integer.class)).orElse(0)
                                ))
                                .all()
                )
                .collectList()
                .map(list -> {
                    int total = list.stream().mapToInt(AlmacenSaldo::saldo).sum();
                    List<InventarioSaldo> porAlmacen = list.stream()
                            .map(a -> new InventarioSaldo(productoUid, a.almacenId, a.saldo))
                            .toList();
                    return new StockRes(productoUid, total, porAlmacen);
                });
    }

    private record AlmacenSaldo(UUID almacenId, int saldo) {}

    public DatabaseClient db() { return db; }


}
