package com.muneebkhawaja.testing.cookbook.unit.mockito;

/// A request to place an order, before persistence.
public record NewOrder(String sku, int quantity) {
}
