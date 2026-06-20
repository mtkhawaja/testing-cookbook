package com.muneebkhawaja.testing.cookbook.unit.mockito;

/// Thrown when inventory cannot satisfy a requested order.
public final class OutOfStockException extends RuntimeException {

    public OutOfStockException(final String sku) {
        super("Out of stock for sku=%s".formatted(sku));
    }
}
