package com.muneebkhawaja.testing.cookbook.unit.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

/// Captures stdout/stderr with Spring Boot's `OutputCaptureExtension`. The `CapturedOutput`
/// parameter is injected per test and exposes everything written to the console (including rendered
/// log lines). Prefer the structured `ListAppender` approach for asserting on log events; reach for
/// this when you genuinely need the raw console text (e.g. third-party code that prints).
@ExtendWith(OutputCaptureExtension.class)
class AccountActivityLoggerOutputCaptureTest {

    private final AccountActivityLogger activityLogger = new AccountActivityLogger();

    @DisplayName("Should write the login message to standard out When a login is recorded")
    @Test
    void shouldWriteTheLoginMessageToStandardOutWhenALoginIsRecorded(final CapturedOutput output) {
        activityLogger.recordLogin("user-42");

        assertThat(output.getOut()).contains("User login succeeded userId=user-42");
    }
}
