package com.omniventas.producto.application;

import com.omniventas.producto.domain.ImagenMeta;
import com.omniventas.producto.domain.ImagenProductoRepository;
import com.omniventas.producto.domain.ImagenStorage;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImagenProductoQueryService {

    public record ResultadoRaw(URI redirect, Path path, MediaType mediaType) {
        public static ResultadoRaw redirect(URI uri) { return new ResultadoRaw(uri, null, null); }
        public static ResultadoRaw file(Path p, MediaType mt) { return new ResultadoRaw(null, p, mt); }
        public boolean isRedirect() { return redirect != null; }
    }

    private final ImagenProductoRepository repo;
    private final ImagenStorage storage;

    public ImagenProductoQueryService(ImagenProductoRepository repo, ImagenStorage storage) {
        this.repo = repo;
        this.storage = storage;
    }

    public Mono<ResultadoRaw> obtenerRaw(UUID productoUid, UUID imagenUid) {
        return repo.findMeta(productoUid, imagenUid)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Imagen no encontrada")))
                .flatMap(meta -> {
                    String url = meta.url();
                    if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                        // Remota → redirigir
                        return Mono.just(ResultadoRaw.redirect(URI.create(url)));
                    }
                    // Local → resolver path
                    String filename = meta.filename() != null ? meta.filename() : "";
                    Path path = storage.resolvePath(productoUid, imagenUid, filename);
                    if (!storage.exists(path)) {
                        return Mono.error(new IllegalStateException("Archivo no encontrado"));
                    }
                    MediaType mt = (meta.contentType() != null ? MediaType.parseMediaType(meta.contentType())
                            : MediaType.APPLICATION_OCTET_STREAM);
                    return Mono.just(ResultadoRaw.file(path, mt));
                });
    }
}
