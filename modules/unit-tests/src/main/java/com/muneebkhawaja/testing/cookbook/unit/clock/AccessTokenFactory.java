package com.muneebkhawaja.testing.cookbook.unit.clock;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/// Issues and inspects [AccessToken]s relative to "now".
///
/// The whole point of this example: time comes from an injected [Clock], never from
/// `Instant.now()`. Production wiring passes `Clock.systemUTC()`; tests pass
/// `Clock.fixed(...)` / `Clock.offset(...)` to make every timestamp deterministic.
public final class AccessTokenFactory {

    private final Clock clock;
    private final Duration timeToLive;

    public AccessTokenFactory(final Clock clock, final Duration timeToLive) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.timeToLive = Objects.requireNonNull(timeToLive, "timeToLive");
    }

    public AccessToken issue(final String subject) {
        if (StringUtils.isBlank(subject)) {
            throw new IllegalArgumentException("subject must not be blank");
        }
        final Instant issuedAt = clock.instant();
        return new AccessToken(subject, issuedAt, issuedAt.plus(timeToLive));
    }

    public boolean isExpired(final AccessToken token) {
        Objects.requireNonNull(token, "token");
        return !clock.instant().isBefore(token.expiresAt());
    }
}
