package com.muneebkhawaja.testing.cookbook.unit.statics;

/// A static-initialization footgun.
///
/// `TOKEN` is computed once, eagerly, the first time the class is loaded. If the required property is
/// absent, class initialization fails — and the failure mode is nasty:
///
/// - the **first** access throws `ExceptionInInitializerError` (wrapping the real cause)
/// - **every later** access throws `NoClassDefFoundError: Could not initialize class ...`
///
/// That second error hides the original cause and is the "dreaded" one people hit in tests: a class
/// that silently refuses to load because its static init blew up earlier (often reading config/env
/// that is present in production but missing under test). Prefer lazy or instance configuration over
/// heavy or failure-prone static initializers.
public final class EagerConfig {

    /// Compile-time constant, so referencing it does NOT trigger class initialization.
    public static final String REQUIRED_PROPERTY = "cookbook.required.token";

    /// Computed (not a constant), so the first read of this field DOES trigger initialization.
    public static final String TOKEN = loadToken();

    private EagerConfig() {
    }

    private static String loadToken() {
        final String token = System.getProperty(REQUIRED_PROPERTY);
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Missing required property: %s".formatted(REQUIRED_PROPERTY));
        }
        return token;
    }
}
