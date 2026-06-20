package com.muneebkhawaja.testing.cookbook.unit.spy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/// A real, simple collaborator with genuine behaviour (it actually stores entries). Spying it lets a
/// test verify interactions AND keep that real behaviour — unlike a plain mock, whose `auditedCount()`
/// would just return the default `0`. Not `final` so Mockito can spy it.
public class EventAuditor {

    private final List<String> entries = new ArrayList<>();

    public void audit(final String action) {
        if (StringUtils.isBlank(action)) {
            throw new IllegalArgumentException("action must not be blank");
        }
        entries.add(action);
    }

    public int auditedCount() {
        return entries.size();
    }
}
