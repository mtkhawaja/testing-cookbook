package com.muneebkhawaja.testing.cookbook.unit.instancio;

import java.util.Objects;

/// Decides whether a [Customer] is an adult. Depends only on `age` — so a test should specify only
/// `age` and let the rest of the customer be generated.
public final class AgePolicy {

    public static final int MINIMUM_ADULT_AGE = 18;

    public boolean isAdult(final Customer customer) {
        Objects.requireNonNull(customer, "customer");
        return customer.age() >= MINIMUM_ADULT_AGE;
    }
}
