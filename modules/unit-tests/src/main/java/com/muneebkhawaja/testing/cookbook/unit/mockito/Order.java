package com.muneebkhawaja.testing.cookbook.unit.mockito;

/// A persisted order. `id` is `null` until the repository assigns one.
public record Order(String id, String sku, int quantity) {
}
