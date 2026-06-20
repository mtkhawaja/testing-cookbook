package com.muneebkhawaja.testing.cookbook.unit.mockito;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/// Demonstrates annotation-driven Mockito via `@ExtendWith(MockitoExtension.class)`:
/// `@Mock` collaborators, `@InjectMocks` subject, `@Captor` for argument capture, and strict stubbing.
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private InventoryClient inventory;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @DisplayName("Should persist the reserved order When inventory is available")
    @Test
    void shouldPersistTheReservedOrderWhenInventoryIsAvailable() {
        when(inventory.reserve("sku-1", 2)).thenReturn(true);
        when(repository.save(orderCaptor.capture())).thenReturn(new Order("ord-1", "sku-1", 2));

        final Order placed = orderService.place(new NewOrder("sku-1", 2));

        assertThat(placed.id()).isEqualTo("ord-1");
        assertThat(orderCaptor.getValue())
                .satisfies(saved -> {
                    assertThat(saved.id()).isNull();
                    assertThat(saved.sku()).isEqualTo("sku-1");
                    assertThat(saved.quantity()).isEqualTo(2);
                });
        verify(inventory).reserve("sku-1", 2);
    }

    @DisplayName("Should throw out of stock and not persist When inventory cannot be reserved")
    @Test
    void shouldThrowOutOfStockAndNotPersistWhenInventoryCannotBeReserved() {
        when(inventory.reserve("sku-1", 5)).thenReturn(false);

        assertThatThrownBy(() -> orderService.place(new NewOrder("sku-1", 5)))
                .isInstanceOf(OutOfStockException.class)
                .hasMessageContaining("sku-1");

        verifyNoInteractions(repository);
    }
}
