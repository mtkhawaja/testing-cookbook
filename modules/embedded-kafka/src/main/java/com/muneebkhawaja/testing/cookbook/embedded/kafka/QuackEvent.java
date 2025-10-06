package com.muneebkhawaja.testing.cookbook.embedded.kafka;

import java.util.UUID;

public record QuackEvent(
        UUID uuid,
        String name,
        String species
) {
}