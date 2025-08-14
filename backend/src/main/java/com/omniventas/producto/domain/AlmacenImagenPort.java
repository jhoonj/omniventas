package com.omniventas.producto.domain;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.UUID;

/** Puerto de almacenamiento de archivos (FS local, S3, etc.). */
public interface AlmacenImagenPort {
    /** Guarda el archivo fisicamente y retorna el Path final. */
    Mono<Path> guardarArchivo(UUID productoUid, UUID imagenUid, String extension, FilePart file);

    /** Elimina el archivo si existe. */
    Mono<Void> eliminarArchivo(UUID productoUid, UUID imagenUid, String extension);

    /** Resuelve el Path local (no asegura existencia). */
    Path resolverPath(UUID productoUid, UUID imagenUid, String extension);
}
