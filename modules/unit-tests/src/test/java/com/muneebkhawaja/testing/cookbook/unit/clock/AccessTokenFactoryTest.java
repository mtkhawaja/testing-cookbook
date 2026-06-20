package com.muneebkhawaja.testing.cookbook.unit.clock;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/// Demonstrates testing clock-based code by injecting a controllable [Clock]
/// (`Clock.fixed` to pin "now", `Clock.offset` to advance it) instead of calling `Instant.now()`.
class AccessTokenFactoryTest {

    private static final Instant NOW = Instant.parse("2025-01-01T00:00:00Z");
    private static final Duration TTL = Duration.ofMinutes(30);

    @DisplayName("Should stamp the token with the clock's time When a token is issued")
    @Test
    void shouldStampTheTokenWithTheClocksTimeWhenATokenIsIssued() {
        final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        final AccessTokenFactory factory = new AccessTokenFactory(clock, TTL);

        final AccessToken token = factory.issue("user-1");

        assertThat(token.subject()).isEqualTo("user-1");
        assertThat(token.issuedAt()).isEqualTo(NOW);
        assertThat(token.expiresAt()).isEqualTo(NOW.plus(TTL));
    }

    @DisplayName("Should not be expired When inspected before the TTL elapses")
    @Test
    void shouldNotBeExpiredWhenInspectedBeforeTheTtlElapses() {
        final AccessToken token = new AccessTokenFactory(Clock.fixed(NOW, ZoneOffset.UTC), TTL).issue("user-1");
        final Clock justBeforeExpiry = Clock.offset(Clock.fixed(NOW, ZoneOffset.UTC), TTL.minusSeconds(1));

        final AccessTokenFactory factory = new AccessTokenFactory(justBeforeExpiry, TTL);

        assertThat(factory.isExpired(token)).isFalse();
    }

    @DisplayName("Should report expiry relative to the clock When the token is inspected")
    @ParameterizedTest(name = "after {0} expired={1}")
    @CsvSource({
            "PT29M, false",
            "PT30M, true",
            "PT31M, true"
    })
    void shouldReportExpiryRelativeToTheClockWhenTheTokenIsInspected(final Duration elapsed, final boolean expired) {
        final AccessToken token = new AccessTokenFactory(Clock.fixed(NOW, ZoneOffset.UTC), TTL).issue("user-1");
        final Clock inspectionClock = Clock.offset(Clock.fixed(NOW, ZoneOffset.UTC), elapsed);

        final AccessTokenFactory factory = new AccessTokenFactory(inspectionClock, TTL);

        assertThat(factory.isExpired(token)).isEqualTo(expired);
    }
}
