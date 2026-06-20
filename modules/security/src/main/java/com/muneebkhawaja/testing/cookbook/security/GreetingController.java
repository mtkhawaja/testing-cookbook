package com.muneebkhawaja.testing.cookbook.security;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/// Endpoints with different access rules, used to exercise URL-level security in tests.
@RestController
public class GreetingController {

    @GetMapping("/public")
    public String publicGreeting() {
        return "public";
    }

    @GetMapping("/private")
    public String privateGreeting(final Principal principal) {
        return "hello %s".formatted(principal.getName());
    }

    @GetMapping("/admin")
    public String adminGreeting() {
        return "admin area";
    }

    @PostMapping("/messages")
    public String createMessage() {
        return "created";
    }
}
