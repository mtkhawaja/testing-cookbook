package com.muneebkhawaja.testing.cookbook.unit.spy;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/// Counts distinct words. `normalize` is a separate, overridable step so a spy can stub just that part
/// (e.g. to skip expensive or non-deterministic normalization) while `countUniqueWords` runs for real.
/// Not `final` so Mockito can spy it.
public class WordCounter {

    public int countUniqueWords(final String text) {
        return (int) normalize(text).stream().distinct().count();
    }

    protected List<String> normalize(final String text) {
        Objects.requireNonNull(text, "text");
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("\\W+"))
                .filter(token -> !token.isBlank())
                .toList();
    }
}
