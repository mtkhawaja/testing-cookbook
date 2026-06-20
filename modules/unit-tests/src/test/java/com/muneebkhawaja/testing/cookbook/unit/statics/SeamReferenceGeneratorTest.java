package com.muneebkhawaja.testing.cookbook.unit.statics;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/// Technique 3 (preferred): design the static away. With the UUID source injected as a seam, the test
/// passes a fixed supplier — no `mockStatic`, no bytecode tricks, no extra dependency.
class SeamReferenceGeneratorTest {

    @DisplayName("Should build the reference from the injected supplier When next is called")
    @Test
    void shouldBuildTheReferenceFromTheInjectedSupplierWhenNextIsCalled() {
        final UUID fixed = UUID.fromString("00000000-0000-0000-0000-0000000000ab");
        final SeamReferenceGenerator generator = new SeamReferenceGenerator(() -> fixed);

        assertThat(generator.next()).isEqualTo("REF-%s".formatted(fixed));
    }
}
