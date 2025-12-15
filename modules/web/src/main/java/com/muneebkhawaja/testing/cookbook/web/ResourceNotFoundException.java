package com.muneebkhawaja.testing.cookbook.web;

/**
 * Domain exception indicating the requested resource was not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
