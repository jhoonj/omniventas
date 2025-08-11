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

    @ExceptionHandler(AuthService.InvalidCredentialsException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleInvalidCreds(ServerWebExchange ex) {
        var err = ApiError.of(401, "Unauthorized", "Credenciales inválidas", ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(err)));
    }

    @ExceptionHandler({ExpiredJwtException.class, SignatureException.class})
    public Mono<ResponseEntity<ApiResponse<Void>>> handleJwtErrors(ServerWebExchange ex, Exception e) {
        var msg = (e instanceof ExpiredJwtException) ? "Token expirado" : "Firma de token inválida";
        var err = ApiError.of(401, "Unauthorized", msg, ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(err)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleIllegalArg(ServerWebExchange ex, IllegalArgumentException e) {
        var err = ApiError.of(400, "Bad Request", e.getMessage(), ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail(err)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleAccessDenied(ServerWebExchange ex) {
        var err = ApiError.of(403, "Forbidden", "Acceso denegado", ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.fail(err)));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleBadRequest(ServerWebExchange ex, Exception e) {
        var err = ApiError.of(400, "Bad Request", e.getMessage(), ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail(err)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidation(ServerWebExchange ex, MethodArgumentNotValidException e) {
        Map<String, String> fields = e.getBindingResult().getFieldErrors()
                .stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a,b)->a));

        var err = ApiError.withDetails(
                400, "Bad Request", "Validación fallida",
                ex.getRequest().getPath().value(),
                Map.of("fields", fields)
        );
        return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail(err)));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleConflict(ServerWebExchange ex, DataIntegrityViolationException e) {
        var err = ApiError.of(409, "Conflict", "Registro duplicado o restricción violada", ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail(err)));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleRSE(ServerWebExchange ex, ResponseStatusException e) {
        HttpStatus status = (HttpStatus) e.getStatusCode();
        var err = ApiError.of(
                status.value(),
                status.getReasonPhrase(),
                e.getReason() == null ? "" : e.getReason(),
                ex.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(e.getStatusCode()).body(ApiResponse.fail(err)));
    }


    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleAll(ServerWebExchange ex, Throwable t) {
        // TODO: loggear stacktrace3
        t.printStackTrace();

        var err = ApiError.of(500, "Internal Server Error", "Ocurrió un error inesperado", ex.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.fail(err)));
    }
}
