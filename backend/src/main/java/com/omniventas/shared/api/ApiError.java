package com.omniventas.shared.api;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, Object> details // <- NUEVO (puede ser null)
) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, null);
    }
    public static ApiError withDetails(int status, String error, String message, String path, Map<String, Object> details) {
        return new ApiError(Instant.now(), status, error, message, path, details);
    }
}
