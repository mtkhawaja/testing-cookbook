package com.muneebkhawaja.testing.cookbook.arch.persistence;

/// Persistence layer. Must not depend on the service or web layers.
public final class GreetingRepository {

    public String findGreeting(final String name) {
        return "Hello, %s".formatted(name);
    }
}
