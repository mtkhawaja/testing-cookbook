package com.muneebkhawaja.testing.cookbook.unit.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static ch.qos.logback.classic.Level.INFO;
import static ch.qos.logback.classic.Level.WARN;
import static org.assertj.core.api.Assertions.assertThat;

/// Tests logging by attaching a Logback `ListAppender` to the subject's logger and asserting on the
/// captured events — level, formatted message, and arguments — without parsing console text.
class AccountActivityLoggerTest {

    private final AccountActivityLogger activityLogger = new AccountActivityLogger();

    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void attachAppender() {
        logger = (Logger) LoggerFactory.getLogger(AccountActivityLogger.class);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        logger.detachAppender(appender);
        appender.stop();
    }

    @DisplayName("Should log an INFO event with the user id When a login is recorded")
    @Test
    void shouldLogAnInfoEventWithTheUserIdWhenALoginIsRecorded() {
        activityLogger.recordLogin("user-42");

        assertThat(appender.list).singleElement().satisfies(event -> {
            assertThat(event.getLevel()).isEqualTo(INFO);
            assertThat(event.getFormattedMessage()).isEqualTo("User login succeeded userId=user-42");
            assertThat(event.getArgumentArray()).containsExactly("user-42");
        });
    }

    @DisplayName("Should log a WARN event with the attempt count When a failed login is recorded")
    @Test
    void shouldLogAWarnEventWithTheAttemptCountWhenAFailedLoginIsRecorded() {
        activityLogger.recordFailedLogin("user-42", 3);

        assertThat(appender.list).singleElement().satisfies(event -> {
            assertThat(event.getLevel()).isEqualTo(WARN);
            assertThat(event.getFormattedMessage()).isEqualTo("User login failed userId=user-42 attempts=3");
        });
    }
}
