package com.muneebkhawaja.testing.cookbook.unit.mockito;

import java.util.Objects;

/// Places orders by reserving inventory then persisting. Both collaborators are interfaces so a
/// unit test can supply Mockito mocks via `@Mock` + `@InjectMocks`.
public final class OrderService {

    private final OrderRepository repository;
    private final InventoryClient inventory;

    public OrderService(final OrderRepository repository, final InventoryClient inventory) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.inventory = Objects.requireNonNull(inventory, "inventory");
    }

    public Order place(final NewOrder request) {
        Objects.requireNonNull(request, "request");
        if (!inventory.reserve(request.sku(), request.quantity())) {
            throw new OutOfStockException(request.sku());
        }
        return repository.save(new Order(null, request.sku(), request.quantity()));
    }
}
