package com.omniventas.producto.infrastructure;

import com.omniventas.producto.application.ImagenProductoQueryService;
import com.omniventas.producto.domain.AlmacenImagenPort;
import com.omniventas.producto.domain.ImagenProducto;
import com.omniventas.producto.domain.ImagenProductoRepository;
import com.omniventas.producto.domain.ImagenProductoService;
import com.omniventas.shared.api.ApiEnvelope;
import com.omniventas.shared.api.ApiError;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
public class ProductoImagenRestController {

    // DTOs limpios para salida → evita problemas de inferencia con Map.of(...)
    public record ImagenDto(
            UUID uid,
            String filename,
            String contentType,
            long sizeBytes,
            boolean principal,
            String altText,
            String url,
            OffsetDateTime createdAt
    ) {
        public static ImagenDto of(ImagenProducto img) {
            return new ImagenDto(
                    img.uid(),
                    img.filename(),
                    img.contentType(),
                    img.sizeBytes(),
                    img.principal(),
                    img.altText(),
                    img.url(),
                    img.createdAt()
            );
        }
    }

    public record RegistrarUrlReq(String url, Boolean principal, String altText) {}

    private final ImagenProductoService service;
    private final ImagenProductoQueryService queryService;

    public ProductoImagenRestController(ImagenProductoRepository repo, AlmacenImagenPort storage,ImagenProductoQueryService queryService) {
        this.service = new ImagenProductoService(repo, storage);
        this.queryService = queryService;
    }

    // ---------- Listar ----------
    @GetMapping("/{productoUid}/imagenes")
    public Mono<ResponseEntity<ApiEnvelope<List<ImagenDto>>>> listar(@PathVariable UUID productoUid) {
        return service.listar(productoUid)
                .map(ImagenDto::of)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiEnvelope.ok(list)));
    }

    // ---------- Subir MULTIPART (archivo) ----------
    @PostMapping(path = "/{productoUid}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ApiEnvelope<ImagenDto>>> subirArchivo(@PathVariable UUID productoUid,
                                                                     @RequestPart("file") FilePart file,
                                                                     @RequestParam(name = "principal", defaultValue = "false") boolean principal,
                                                                     @RequestParam(name = "altText", required = false) String altText) {
        if (file == null || !StringUtils.hasText(file.filename())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo requerido"));
        }
        var cmd = new ImagenProductoService.UploadArchivoCmd(productoUid, file, principal, altText);
        return service.subirDesdeArchivo(cmd)
                .map(img -> ResponseEntity
                        .created(URI.create(String.format("/api/productos/%s/imagenes/%s", productoUid, img.uid())))
                        .body(ApiEnvelope.ok(ImagenDto.of(img))))
                .onErrorResume(ex -> {
                    var err = ApiError.of(400, "Bad Request", ex.getMessage(),
                            String.format("/api/productos/%s/imagenes", productoUid));
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiEnvelope.error(err)));
                });
    }

    // ---------- Registrar por URL (JSON) ----------
    @PostMapping(path = "/{productoUid}/imagenes:url", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiEnvelope<ImagenDto>>> registrarUrl(@PathVariable UUID productoUid,
                                                                     @RequestBody RegistrarUrlReq req) {
        if (req == null || !StringUtils.hasText(req.url())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "url requerida"));
        }
        var cmd = new ImagenProductoService.UploadUrlCmd(
                productoUid,
                req.url(),
                Boolean.TRUE.equals(req.principal()),
                req.altText()
        );
        return service.registrarDesdeUrl(cmd)
                .map(img -> ResponseEntity
                        .created(URI.create(String.format("/api/productos/%s/imagenes/%s", productoUid, img.uid())))
                        .body(ApiEnvelope.ok(ImagenDto.of(img))))
                .onErrorResume(ex -> {
                    var err = ApiError.of(400, "Bad Request", ex.getMessage(),
                            String.format("/api/productos/%s/imagenes:url", productoUid));
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiEnvelope.error(err)));
                });
    }

    @GetMapping("/{productoUid}/imagenes/{imagenUid}/raw")
    public Mono<ResponseEntity<?>> raw(@PathVariable UUID productoUid,
                                       @PathVariable UUID imagenUid) {
        return queryService.obtenerRaw(productoUid, imagenUid)
                .map(r -> {
                    if (r.isRedirect()) {
                        return ResponseEntity.status(HttpStatus.FOUND).location(r.redirect()).build();
                    }
                    var res = new FileSystemResource(r.path());
                    return ResponseEntity.ok().contentType(r.mediaType()).body(res);
                });
    }

    // ---------- Marcar como principal ----------
    @PutMapping("/{productoUid}/imagenes/{imagenUid}/principal")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> principal(@PathVariable UUID productoUid, @PathVariable UUID imagenUid) {
        return service.marcarPrincipal(productoUid, imagenUid);
    }

    // ---------- Eliminar ----------
    @DeleteMapping("/{productoUid}/imagenes/{imagenUid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable UUID productoUid, @PathVariable UUID imagenUid) {
        return service.eliminar(productoUid, imagenUid);
    }
}
