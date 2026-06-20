package com.muneebkhawaja.testing.cookbook.unit.statics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Technique 1: a pure static utility is tested by just calling it — no mocking needed.
class SlugsTest {

    @DisplayName("Should convert text to a slug When given printable text")
    @ParameterizedTest(name = "\"{0}\" -> \"{1}\"")
    @CsvSource({
            "Hello World, hello-world",
            "Spring Boot 4, spring-boot-4",
            "a@@@b, a-b",
            "--Edge--, edge"
    })
    void shouldConvertTextToASlugWhenGivenPrintableText(final String input, final String expected) {
        assertThat(Slugs.toSlug(input)).isEqualTo(expected);
    }

    @DisplayName("Should reject the input When it is blank")
    @Test
    void shouldRejectTheInputWhenItIsBlank() {
        assertThatThrownBy(() -> Slugs.toSlug("   ")).isInstanceOf(IllegalArgumentException.class);
    }
}
