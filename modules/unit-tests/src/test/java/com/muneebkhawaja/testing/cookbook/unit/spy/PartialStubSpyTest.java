package com.muneebkhawaja.testing.cookbook.unit.spy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/// Partial stubbing: stub one part of a real object and let the rest run. Also shows the spy footgun —
/// `when(spy.method())` evaluates the real method first.
class PartialStubSpyTest {

    @DisplayName("Should use the stubbed part while running the real method When the spy is partially stubbed")
    @Test
    void shouldUseTheStubbedPartWhileRunningTheRealMethodWhenTheSpyIsPartiallyStubbed() {
        final WordCounter counter = spy(new WordCounter());
        doReturn(List.of("alpha", "beta", "alpha")).when(counter).normalize("anything");

        // Real countUniqueWords runs, but on the stubbed normalize output: distinct of 3 -> 2.
        assertThat(counter.countUniqueWords("anything")).isEqualTo(2);
    }

    @DisplayName("Should invoke the real method When stubbing a spy with when/thenReturn")
    @Test
    void shouldInvokeTheRealMethodWhenStubbingASpyWithWhenThenReturn() {
        final WordCounter counter = spy(new WordCounter());

        // when(counter.normalize(null)) calls the REAL normalize(null) first -> NPE. Use doReturn instead.
        assertThatThrownBy(() -> when(counter.normalize(null)).thenReturn(List.of()))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("Should not invoke the real method When stubbing a spy with doReturn")
    @Test
    void shouldNotInvokeTheRealMethodWhenStubbingASpyWithDoReturn() {
        final WordCounter counter = spy(new WordCounter());
        doReturn(List.of("only")).when(counter).normalize(null);

        assertThat(counter.countUniqueWords(null)).isEqualTo(1);
    }
}
