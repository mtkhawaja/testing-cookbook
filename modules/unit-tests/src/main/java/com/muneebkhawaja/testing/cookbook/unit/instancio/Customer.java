package com.muneebkhawaja.testing.cookbook.unit.instancio;

/// A customer with several fields. Most are irrelevant to any single behaviour under test, which is
/// exactly why auto-generating them (Instancio) keeps tests focused on the field that matters.
public record Customer(String id, String firstName, String lastName, String email, int age) {
}
