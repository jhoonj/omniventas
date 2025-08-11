package com.omniventas.shared.api;

/**
 * Envoltorio genérico para respuestas homogéneas:
 *  - success: indica si la operación fue exitosa
 *  - data: payload (cuando success=true)
 *  - error: detalle de error (cuando success=false)
 */
public record ApiEnvelope<T>(
        boolean success,
        T data,
        ApiError error
) {
    public static <T> ApiEnvelope<T> ok(T data) {
        return new ApiEnvelope<>(true, data, null);
    }
    public static <T> ApiEnvelope<T> error(ApiError error) {
        return new ApiEnvelope<>(false, null, error);
    }
}
