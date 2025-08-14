package com.omniventas.producto.domain;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ImagenProductoService {

    public record UploadArchivoCmd(
            UUID productoUid,
            FilePart file,
            boolean principal,
            String altText
    ) {}

    public record UploadUrlCmd(
            UUID productoUid,
            String url,
            boolean principal,
            String altText
    ) {}

    private final ImagenProductoRepository repo;
    private final AlmacenImagenPort storage;

    public ImagenProductoService(ImagenProductoRepository repo, AlmacenImagenPort storage) {
        this.repo = repo;
        this.storage = storage;
    }

    public Flux<ImagenProducto> listar(UUID productoUid) {
        return repo.listar(productoUid);
    }

    public Mono<ImagenProducto> subirDesdeArchivo(UploadArchivoCmd cmd) {
        Objects.requireNonNull(cmd.file(), "Archivo requerido");
        final UUID imagenUid = UUID.randomUUID();
        final String ext = extensionFrom(cmd.file().filename());
        final String safeName = sanitizeFilename(cmd.file().filename());

        return repo.resolveProductoId(cmd.productoUid())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado")))
                .flatMap(pid -> storage.guardarArchivo(cmd.productoUid(), imagenUid, ext, cmd.file())
                        .then(Mono.defer(() -> sizeOf(storage.resolverPath(cmd.productoUid(), imagenUid, ext))))
                        .flatMap(size -> {
                            // 👇 URL pública relativa al endpoint RAW (cumple NOT NULL)
                            String publicUrl = "/api/productos/" + cmd.productoUid()
                                    + "/imagenes/" + imagenUid + "/raw";

                            ImagenProducto meta = new ImagenProducto(
                                    null, imagenUid, cmd.productoUid(), safeName,
                                    contentTypeOf(cmd.file()), size, cmd.principal(), cmd.altText(),
                                    publicUrl, null
                            );
                            return repo.insert(pid, meta);
                        })
                        .flatMap(img -> ensurePrincipalIfNeeded(img, pid))
                        .onErrorResume(ex -> storage.eliminarArchivo(cmd.productoUid(), imagenUid, ext)
                                .onErrorResume(e -> Mono.empty())
                                .then(Mono.error(ex)))
                );
    }

    public Mono<ImagenProducto> registrarDesdeUrl(UploadUrlCmd cmd) {
        Objects.requireNonNull(cmd.url(), "url requerida");
        final UUID imagenUid = UUID.randomUUID();

        return repo.resolveProductoId(cmd.productoUid())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado")))
                .flatMap(pid -> {
                    ImagenProducto meta = new ImagenProducto(
                            null, imagenUid, cmd.productoUid(), null,
                            null, 0L, cmd.principal(), cmd.altText(),
                            cmd.url(), null
                    );
                    return repo.insert(pid, meta).flatMap(img -> ensurePrincipalIfNeeded(img, pid));
                });
    }

    public Mono<FileSystemResource> descargarRaw(UUID productoUid, UUID imagenUid) {
        return repo.findByUids(productoUid, imagenUid)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Imagen no encontrada")))
                .flatMap(img -> {
                    if (img.url() != null && !img.url().isBlank()) {
                        // No tenemos proxy/redirect aquí; el controller puede decidir redirigir.
                        return Mono.error(new IllegalStateException("Imagen es remota; usar URL."));
                    }
                    final String ext = extensionFrom(Optional.ofNullable(img.filename()).orElse(""));
                    final Path path = storage.resolverPath(productoUid, imagenUid, ext);
                    return Mono.fromCallable(() -> Files.exists(path) ? new FileSystemResource(path) : null)
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(res -> res != null ? Mono.just(res) : Mono.error(new IllegalArgumentException("Archivo no encontrado")));
                });
    }

    public Mono<Void> marcarPrincipal(UUID productoUid, UUID imagenUid) {
        return repo.resolveProductoId(productoUid)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado")))
                .flatMap(pid -> repo.marcarPrincipal(pid, imagenUid));
    }

    public Mono<Void> eliminar(UUID productoUid, UUID imagenUid) {
        return repo.findByUids(productoUid, imagenUid)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Imagen no encontrada")))
                .flatMap(img -> {
                    final String ext = extensionFrom(Optional.ofNullable(img.filename()).orElse(""));
                    return repo.eliminar(productoUid, imagenUid)
                            .then(storage.eliminarArchivo(productoUid, imagenUid, ext).onErrorResume(e -> Mono.empty()));
                });
    }

    // ---- helpers ----
    private Mono<Long> sizeOf(Path path) {
        return Mono.fromCallable(() -> Files.size(path)).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<ImagenProducto> ensurePrincipalIfNeeded(ImagenProducto img, Long productoId) {
        if (img.principal()) {
            return repo.limpiarPrincipalesExcepto(productoId, img.uid()).thenReturn(img);
        }
        return Mono.just(img);
    }

    private static String contentTypeOf(FilePart file) {
        return file.headers() != null && file.headers().getContentType() != null
                ? file.headers().getContentType().toString()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    private static String extensionFrom(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return (i >= 0 ? filename.substring(i) : "");
    }

    private static String sanitizeFilename(String name) {
        if (name == null) return null;
        return name.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
