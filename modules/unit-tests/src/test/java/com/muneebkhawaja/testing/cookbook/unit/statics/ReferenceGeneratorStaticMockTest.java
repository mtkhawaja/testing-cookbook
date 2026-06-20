package com.muneebkhawaja.testing.cookbook.unit.statics;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

/// Technique 2: when code calls a `static` you cannot change, stub it with Mockito's `mockStatic`.
///
/// Keep the scope tight: inside the try-with-resources, ALL static calls to the mocked class (on this
/// thread) are intercepted, so build any real values you need (here, `fixed`) BEFORE opening it —
/// otherwise `UUID.fromString(...)` would itself be mocked.
class ReferenceGeneratorStaticMockTest {

    @DisplayName("Should build the reference from the generated UUID When next is called")
    @Test
    void shouldBuildTheReferenceFromTheGeneratedUuidWhenNextIsCalled() {
        final UUID fixed = UUID.fromString("00000000-0000-0000-0000-0000000000ab");

        try (final MockedStatic<UUID> uuids = mockStatic(UUID.class)) {
            uuids.when(UUID::randomUUID).thenReturn(fixed);

            final String reference = new ReferenceGenerator().next();

            assertThat(reference).isEqualTo("REF-%s".formatted(fixed));
            uuids.verify(UUID::randomUUID);
        }
    }
}
