package com.muneebkhawaja.testing.cookbook.unit.statics;

import java.util.Locale;
import java.util.regex.Pattern;

/// A pure static utility. Static methods that are pure functions of their inputs need no mocking at
/// all — the test just calls them.
public final class Slugs {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final Pattern EDGE_DASHES = Pattern.compile("(^-|-$)");

    private Slugs() {
    }

    public static String toSlug(final String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("input must not be blank");
        }
        final String lowered = input.trim().toLowerCase(Locale.ROOT);
        final String dashed = NON_ALPHANUMERIC.matcher(lowered).replaceAll("-");
        return EDGE_DASHES.matcher(dashed).replaceAll("");
    }
}
