package com.dolsk.tyres.exception;

/**
 * Thrown when an operation is blocked by an existing relationship.
 * Example: deleting a Tyre that is still referenced by active Orders.
 * Maps to HTTP 409 Conflict in GlobalExceptionHandler.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
