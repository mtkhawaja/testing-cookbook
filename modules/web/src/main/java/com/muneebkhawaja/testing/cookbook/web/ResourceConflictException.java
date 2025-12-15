package com.muneebkhawaja.testing.cookbook.web;

/**
 * Domain exception indicating a conflict with the current state of the resource (e.g., duplicates).
 */
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(final String message) {
        super(message);
    }
}
