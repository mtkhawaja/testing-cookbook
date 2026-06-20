package com.muneebkhawaja.testing.cookbook.arch.web;

import java.util.Objects;

import com.muneebkhawaja.testing.cookbook.arch.service.GreetingService;

/// Web layer. May use the service layer; nothing may depend back on it.
public final class GreetingController {

    private final GreetingService service;

    public GreetingController(final GreetingService service) {
        this.service = Objects.requireNonNull(service, "service");
    }

    public String greeting(final String name) {
        return service.greet(name);
    }
}
