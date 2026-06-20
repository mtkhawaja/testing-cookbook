package com.muneebkhawaja.testing.cookbook.unit.mockito;

import java.util.Optional;

/// Persistence collaborator for [Order]s. Mocked in [OrderService] unit tests.
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(String id);
}
