package com.omniventas.producto.domain;

import java.nio.file.Path;
import java.util.UUID;

public interface ImagenStorage {
    Path resolvePath(UUID productoUid, UUID imagenUid, String filenameOrExt);
    boolean exists(Path path);
}
