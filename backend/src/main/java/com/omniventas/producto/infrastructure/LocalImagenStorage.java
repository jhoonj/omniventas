package com.omniventas.producto.infrastructure;

import com.omniventas.producto.domain.ImagenStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalImagenStorage implements ImagenStorage {

    private final Path root;

    public LocalImagenStorage(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public Path resolvePath(UUID productoUid, UUID imagenUid, String filenameOrExt) {
        String ext = extractExt(filenameOrExt);
        return root.resolve("productos")
                .resolve(productoUid.toString())
                .resolve(imagenUid.toString() + (ext.isEmpty() ? "" : ext));
    }

    @Override
    public boolean exists(Path path) {
        return Files.exists(path);
    }

    private static String extractExt(String name) {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        return (i >= 0 ? name.substring(i) : "");
    }
}
