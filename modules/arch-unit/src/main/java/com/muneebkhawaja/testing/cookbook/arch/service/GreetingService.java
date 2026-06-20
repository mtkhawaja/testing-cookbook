package com.muneebkhawaja.testing.cookbook.arch.service;

import java.util.Objects;

import com.muneebkhawaja.testing.cookbook.arch.persistence.GreetingRepository;

/// Service layer. May use persistence, must not depend on web.
public final class GreetingService {

    private final GreetingRepository repository;

    public GreetingService(final GreetingRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public String greet(final String name) {
        return repository.findGreeting(name);
    }
}
