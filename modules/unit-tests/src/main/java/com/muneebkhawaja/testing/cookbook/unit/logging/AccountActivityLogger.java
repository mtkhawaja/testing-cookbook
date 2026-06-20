package com.muneebkhawaja.testing.cookbook.unit.logging;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Emits account-activity log events through SLF4J (parameterized messages, never `System.out`).
///
/// Used to show two ways to test logging:
/// - assert on structured log events with a Logback `ListAppender`
/// - assert on rendered stdout with Spring Boot's `OutputCaptureExtension`
public final class AccountActivityLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountActivityLogger.class);

    public void recordLogin(final String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        LOGGER.info("User login succeeded userId={}", userId);
    }

    public void recordFailedLogin(final String userId, final int attempts) {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        LOGGER.warn("User login failed userId={} attempts={}", userId, attempts);
    }
}
