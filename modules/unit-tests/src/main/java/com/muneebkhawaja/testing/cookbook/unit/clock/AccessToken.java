package com.muneebkhawaja.testing.cookbook.unit.clock;

import java.time.Instant;

/// An issued access token with its issue and expiry instants (UTC).
///
/// Timestamps are produced from an injected [java.time.Clock] so they are deterministic in tests.
public record AccessToken(String subject, Instant issuedAt, Instant expiresAt) {
}
