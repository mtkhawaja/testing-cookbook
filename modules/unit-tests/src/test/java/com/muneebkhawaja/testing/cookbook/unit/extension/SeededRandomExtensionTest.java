package com.muneebkhawaja.testing.cookbook.unit.extension;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/// Exercises the custom [SeededRandomExtension]: it resolves `int` parameters annotated with
/// [RandomInt] and, because the seed is fixed, the injected values are deterministic.
@ExtendWith(SeededRandomExtension.class)
class SeededRandomExtensionTest {

    @DisplayName("Should inject an int within the configured range When the parameter is annotated")
    @Test
    void shouldInjectAnIntWithinTheConfiguredRangeWhenTheParameterIsAnnotated(@RandomInt(min = 1, max = 7) final int dieRoll) {
        assertThat(dieRoll).isBetween(1, 6);
    }

    @DisplayName("Should inject a deterministic value When the seed is fixed")
    @Test
    void shouldInjectADeterministicValueWhenTheSeedIsFixed(@RandomInt final int value) {
        final int expected = new Random(SeededRandomExtension.SEED).nextInt(0, 100);

        assertThat(value).isEqualTo(expected);
    }
}
