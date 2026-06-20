package com.muneebkhawaja.testing.cookbook.unit.statics;

import java.util.UUID;

/// Calls a `static` method (`UUID.randomUUID()`) directly, so its output is non-deterministic and
/// there is no seam to stub. Testing it requires Mockito's `mockStatic`. Compare with
/// [SeamReferenceGenerator], which is designed so no static mocking is needed.
public final class ReferenceGenerator {

    public String next() {
        return "REF-%s".formatted(UUID.randomUUID());
    }
}
