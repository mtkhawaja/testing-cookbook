package com.muneebkhawaja.testing.cookbook.unit.mockito;

/// Remote inventory collaborator. Mocked in [OrderService] unit tests.
public interface InventoryClient {

    /// Attempts to reserve `quantity` units of `sku`, returning whether the reservation succeeded.
    boolean reserve(String sku, int quantity);
}
