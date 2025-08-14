package com.omniventas.producto.infrastructure;

import com.omniventas.producto.domain.ImagenProducto;
import com.omniventas.producto.domain.ImagenProductoRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.omniventas.producto.domain.ImagenMeta;

import java.util.UUID;

@Repository
public class R2dbcImagenProductoAdapter implements ImagenProductoRepository {

    private final DatabaseClient db;

    public R2dbcImagenProductoAdapter(DatabaseClient db) {
        this.db = db;
    }

    @Override
    public Mono<Long> resolveProductoId(UUID productoUid) {
        return db.sql("SELECT id FROM public.productos WHERE uid = :uid")
                .bind("uid", productoUid)
                .map((row, meta) -> row.get("id", Long.class))
                .one();
    }

    // imports necesarios arriba del archivo


    // dentro de R2dbcImagenProductoAdapter
    @Override
    public Mono<ImagenMeta> findMeta(UUID productoUid, UUID imagenUid) {
        return findByUids(productoUid, imagenUid)
                .map(img -> new ImagenMeta(
                        img.filename(),
                        img.contentType(),
                        img.url()
                ));
    }



    @Override
    public Flux<ImagenProducto> listar(UUID productoUid) {
        String sql = """
            SELECT i.id, i.uid, p.uid as producto_uid, i.filename, i.content_type, i.size_bytes,
                   i.principal, i.alt_text, i.url, i.created_at
              FROM public.producto_imagenes i
              JOIN public.productos p ON p.id = i.producto_id
             WHERE p.uid = :puid
             ORDER BY i.principal DESC, i.created_at DESC, i.id DESC
        """;
        return db.sql(sql)
                .bind("puid", productoUid)
                .map((row, meta) -> new ImagenProducto(
                        row.get("id", Long.class),
                        row.get("uid", java.util.UUID.class),
                        row.get("producto_uid", java.util.UUID.class),
                        row.get("filename", String.class),
                        row.get("content_type", String.class),
                        row.get("size_bytes", Long.class) == null ? 0L : row.get("size_bytes", Long.class),
                        Boolean.TRUE.equals(row.get("principal", Boolean.class)),
                        row.get("alt_text", String.class),
                        row.get("url", String.class),
                        row.get("created_at", java.time.OffsetDateTime.class)
                ))
                .all();
    }

    @Override
    public Mono<ImagenProducto> insert(Long productoId, ImagenProducto meta) {
        String sql = """
        INSERT INTO public.producto_imagenes (producto_id, uid, filename, content_type, size_bytes, principal, alt_text, url)
        VALUES (:pid, :uid, :fn, :ct, :sz, :pr, :alt, :url)
        RETURNING id, uid, filename, content_type, size_bytes, principal, alt_text, url, created_at
    """;

        var spec = db.sql(sql)
                .bind("pid", productoId)
                .bind("uid", meta.uid());

        if (meta.filename() != null) spec = spec.bind("fn", meta.filename()); else spec = spec.bindNull("fn", String.class);
        if (meta.contentType() != null) spec = spec.bind("ct", meta.contentType()); else spec = spec.bindNull("ct", String.class);
        spec = spec.bind("sz", meta.sizeBytes());
        spec = spec.bind("pr", meta.principal());
        if (meta.altText() != null) spec = spec.bind("alt", meta.altText()); else spec = spec.bindNull("alt", String.class);
        if (meta.url() != null) spec = spec.bind("url", meta.url()); else spec = spec.bindNull("url", String.class);

        return spec.map((row, m) -> new ImagenProducto(
                        row.get("id", Long.class),
                        row.get("uid", java.util.UUID.class),
                        meta.productoUid(),                           // <- usamos el que ya tenemos
                        row.get("filename", String.class),
                        row.get("content_type", String.class),
                        row.get("size_bytes", Long.class) == null ? 0L : row.get("size_bytes", Long.class),
                        Boolean.TRUE.equals(row.get("principal", Boolean.class)),
                        row.get("alt_text", String.class),
                        row.get("url", String.class),
                        row.get("created_at", java.time.OffsetDateTime.class)
                ))
                .one();
    }

    @Override
    public Mono<Void> marcarPrincipal(Long productoId, UUID imagenUid) {
        String clear = "UPDATE public.producto_imagenes SET principal = false WHERE producto_id = :pid";
        String set   = "UPDATE public.producto_imagenes SET principal = true  WHERE producto_id = :pid AND uid = :iu";
        return db.sql(clear).bind("pid", productoId).fetch().rowsUpdated()
                .then(db.sql(set).bind("pid", productoId).bind("iu", imagenUid).fetch().rowsUpdated())
                .then();
    }

    @Override
    public Mono<Void> limpiarPrincipalesExcepto(Long productoId, UUID imagenUid) {
        String clearOthers = """
            UPDATE public.producto_imagenes
               SET principal = false
             WHERE producto_id = :pid AND uid <> :iu
        """;
        return db.sql(clearOthers)
                .bind("pid", productoId)
                .bind("iu", imagenUid)
                .fetch().rowsUpdated()
                .then();
    }

    @Override
    public Mono<ImagenProducto> findByUids(UUID productoUid, UUID imagenUid) {
        String sql = """
            SELECT i.id, i.uid, p.uid as producto_uid, i.filename, i.content_type, i.size_bytes,
                   i.principal, i.alt_text, i.url, i.created_at
              FROM public.producto_imagenes i
              JOIN public.productos p ON p.id = i.producto_id
             WHERE i.uid = :iu AND p.uid = :puid
        """;
        return db.sql(sql)
                .bind("iu", imagenUid)
                .bind("puid", productoUid)
                .map((row, meta) -> new ImagenProducto(
                        row.get("id", Long.class),
                        row.get("uid", java.util.UUID.class),
                        row.get("producto_uid", java.util.UUID.class),
                        row.get("filename", String.class),
                        row.get("content_type", String.class),
                        row.get("size_bytes", Long.class) == null ? 0L : row.get("size_bytes", Long.class),
                        Boolean.TRUE.equals(row.get("principal", Boolean.class)),
                        row.get("alt_text", String.class),
                        row.get("url", String.class),
                        row.get("created_at", java.time.OffsetDateTime.class)
                ))
                .one();
    }

    @Override
    public Mono<Void> eliminar(UUID productoUid, UUID imagenUid) {
        String sql = """
            DELETE FROM public.producto_imagenes
             WHERE uid = :iu
               AND producto_id = (SELECT id FROM public.productos WHERE uid = :puid)
        """;
        return db.sql(sql)
                .bind("iu", imagenUid)
                .bind("puid", productoUid)
                .fetch()
                .rowsUpdated()
                .then();
    }
}
