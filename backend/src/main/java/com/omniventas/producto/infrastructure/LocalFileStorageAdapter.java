package com.omniventas.producto.infrastructure;

import com.omniventas.producto.domain.AlmacenImagenPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalFileStorageAdapter implements AlmacenImagenPort {

    private final Path rootDir;

    public LocalFileStorageAdapter(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.rootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public Mono<Path> guardarArchivo(UUID productoUid, UUID imagenUid, String extension, FilePart file) {
        Path dir = resolverDir(productoUid);
        Path path = resolverPath(productoUid, imagenUid, extension);
        return Mono.fromCallable(() -> { Files.createDirectories(dir); return dir; })
                .subscribeOn(Schedulers.boundedElastic())
                .then(file.transferTo(path))
                .thenReturn(path);
    }

    @Override
    public Mono<Void> eliminarArchivo(UUID productoUid, UUID imagenUid, String extension) {
        Path path = resolverPath(productoUid, imagenUid, extension);
        return Mono.fromRunnable(() -> { try { Files.deleteIfExists(path); } catch (Exception ignore) {} })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Path resolverPath(UUID productoUid, UUID imagenUid, String extension) {
        return resolverDir(productoUid).resolve(imagenUid.toString() + (extension == null ? "" : extension));
    }

    private Path resolverDir(UUID productoUid) {
        return rootDir.resolve("productos").resolve(productoUid.toString());
    }
}
