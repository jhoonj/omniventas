package com.omniventas.shared.api;

import com.omniventas.security.jwt.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalErrorHandler {

    // 401 – credenciales malas (tu caso actual)
    @ExceptionHandler(AuthService.InvalidCredentialsException.class)
    public Mono<ResponseEntity<ApiError>> handleInvalidCreds(ServerWebExchange ex) {
        var body = ApiError.of(401, "Unauthorized", "Credenciales inválidas", ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body));
    }

    // 401 – JWT expirado/firma inválida
    @ExceptionHandler({ExpiredJwtException.class, SignatureException.class, IllegalArgumentException.class})
    public Mono<ResponseEntity<ApiError>> handleJwtErrors(ServerWebExchange ex, Exception e) {
        var msg = (e instanceof ExpiredJwtException) ? "Token expirado" :
                (e instanceof SignatureException) ? "Firma de token inválida" :
                        "Token inválido";
        var body = ApiError.of(401, "Unauthorized", msg, ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body));
    }

    // 403 – sin permisos
    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ApiError>> handleAccessDenied(ServerWebExchange ex) {
        var body = ApiError.of(403, "Forbidden", "Acceso denegado", ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(body));
    }

    // 400 – body/query inválidos o tipos incorrectos
    @ExceptionHandler({ServerWebInputException.class})
    public Mono<ResponseEntity<ApiError>> handleBadRequest(ServerWebExchange ex, Exception e) {
        var body = ApiError.of(400, "Bad Request", e.getMessage(), ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.badRequest().body(body));
    }

    // 400 – validación con @Valid (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<Object>> handleValidation(ServerWebExchange ex, MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors()
                .stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a,b)->a));
        return Mono.just(ResponseEntity.badRequest().body(Map.of(
                "timestamp", java.time.Instant.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", "Validación fallida",
                "path", ex.getRequest().getPath().value(),
                "errors", errors
        )));
    }

    // 409 – violación de unicidad (roles.nombre, usuarios.email, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ApiError>> handleConflict(ServerWebExchange ex, DataIntegrityViolationException e) {
        var body = ApiError.of(409, "Conflict", "Registro duplicado o restricción violada", ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(body));
    }

    // Propaga ResponseStatusException tal cual (404, 422, etc.)
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ApiError>> handleRSE(ServerWebExchange ex, ResponseStatusException e) {

        HttpStatus status = (HttpStatus) e.getStatusCode();
        var body = ApiError.of(
                status.value(),
                status.getReasonPhrase(), // ahora sí existe
                e.getReason() == null ? "" : e.getReason(),
                ex.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(e.getStatusCode()).body(body));
    }

    // 500 – fallback
    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ApiError>> handleAll(ServerWebExchange ex, Throwable t) {
        // TODO: loggear stacktrace con tu logger
        var body = ApiError.of(500, "Internal Server Error", "Ocurrió un error inesperado", ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
    }
}
