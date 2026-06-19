package com.dolsk.tyres.exception;

import com.dolsk.tyres.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * Centralised exception → HTTP response mapping.
 *
 * Every error returns: { "success": false, "data": null, "message": "user-friendly reason" }
 * Internal details are logged but NEVER exposed to clients.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── 400 Bad Request ───────────────────────────────────────────────────────

    /** @Valid / @NotBlank / @NotNull annotation failures */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ApiResponse.error(message);
    }

    /** Missing required query parameters */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMissingParam(MissingServletRequestParameterException ex) {
        return ApiResponse.error("Missing required parameter: " + ex.getParameterName());
    }

    /** Type mismatch — e.g. passing "abc" where a Long is expected */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ApiResponse.error("Invalid value for parameter '" + ex.getName()
                + "': expected type " + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"));
    }

    /** IllegalArgumentException from service layer */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBadRequest(IllegalArgumentException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    // ── 401 Unauthorized ──────────────────────────────────────────────────────

    /** Bad login credentials or wrong current password */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleUnauthorized(UsernameNotFoundException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    // ── 403 Forbidden ─────────────────────────────────────────────────────────

    /** @PreAuthorize fails — user doesn't have the required role */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleAccessDenied(AccessDeniedException ex) {
        return ApiResponse.error("You do not have permission to perform this action");
    }

    // ── 404 Not Found ─────────────────────────────────────────────────────────

    /** Entity not found in database */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFound(ResourceNotFoundException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    // ── 405 Method Not Allowed ────────────────────────────────────────────────

    /** Wrong HTTP method (e.g. GET on a POST-only endpoint) */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<Void> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ApiResponse.error("HTTP method " + ex.getMethod() + " is not supported for this endpoint");
    }

    // ── 409 Conflict ──────────────────────────────────────────────────────────

    /** Duplicate username */
    @ExceptionHandler(DuplicateFoundException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleDuplicate(DuplicateFoundException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    /** Delete blocked by foreign key relationship */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleConflict(ConflictException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    // ── 500 Internal Server Error ─────────────────────────────────────────────

    /** Catch-all — logs full trace, returns generic message to client */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneric(Exception ex) {
        logger.error("Unhandled exception: {} — {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return ApiResponse.error("An unexpected error occurred. Please try again later.");
    }
}
