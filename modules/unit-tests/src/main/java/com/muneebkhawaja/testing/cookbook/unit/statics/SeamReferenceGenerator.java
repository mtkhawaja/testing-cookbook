package com.muneebkhawaja.testing.cookbook.unit.statics;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

/// The same behaviour as [ReferenceGenerator], but the `static` call is replaced with an injected
/// seam (`Supplier<UUID>`). Production wires `UUID::randomUUID`; tests pass a fixed supplier — no
/// static mocking, no extra dependency. Prefer this whenever you own the code.
public final class SeamReferenceGenerator {

    private final Supplier<UUID> uuids;

    public SeamReferenceGenerator(final Supplier<UUID> uuids) {
        this.uuids = Objects.requireNonNull(uuids, "uuids");
    }

    public String next() {
        return "REF-%s".formatted(uuids.get());
    }
}
