package com.dolsk.tyres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Uniform API envelope returned by every endpoint.
 *
 * Success:  { "success": true,  "data": {...}, "message": null }
 * Failure:  { "success": false, "data": null,  "message": "reason" }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;

    /** Convenience factory — error response (used by GlobalExceptionHandler). */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    /** Convenience factory — success with data, no message. */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /** Convenience factory — success with data and a message. */
    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }
}
