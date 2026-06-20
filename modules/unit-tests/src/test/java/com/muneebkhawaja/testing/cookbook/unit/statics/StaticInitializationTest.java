package com.muneebkhawaja.testing.cookbook.unit.statics;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Technique 4 (the footgun): demonstrates how a failing `static` initializer produces the dreaded
/// `NoClassDefFoundError: Could not initialize class ...`.
///
/// Each test loads [EagerConfig] in a fresh `URLClassLoader` so class initialization is isolated and
/// the order is controlled (a class initializes once per classloader). Referencing the compile-time
/// constant `EagerConfig.REQUIRED_PROPERTY` does not load the class, so the app classloader's copy is
/// never touched.
class StaticInitializationTest {

    private static final String CLASS_NAME = "com.muneebkhawaja.testing.cookbook.unit.statics.EagerConfig";

    @DisplayName("Should throw ExceptionInInitializerError then NoClassDefFoundError When static init fails")
    @Test
    void shouldThrowExceptionInInitializerErrorThenNoClassDefFoundErrorWhenStaticInitFails() throws Exception {
        System.clearProperty(EagerConfig.REQUIRED_PROPERTY);
        try (final URLClassLoader loader = freshClassLoader()) {
            // First access triggers initialization, which fails and surfaces the real cause.
            assertThatThrownBy(() -> Class.forName(CLASS_NAME, true, loader))
                    .isInstanceOf(ExceptionInInitializerError.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            // The class is now marked erroneous; every later access throws this instead — cause lost.
            assertThatThrownBy(() -> Class.forName(CLASS_NAME, true, loader))
                    .isInstanceOf(NoClassDefFoundError.class)
                    .hasMessageContaining("Could not initialize class");
        }
    }

    @DisplayName("Should initialize successfully When the required property is set before first access")
    @Test
    void shouldInitializeSuccessfullyWhenTheRequiredPropertyIsSetBeforeFirstAccess() throws Exception {
        System.setProperty(EagerConfig.REQUIRED_PROPERTY, "secret");
        try (final URLClassLoader loader = freshClassLoader()) {
            final Class<?> type = Class.forName(CLASS_NAME, true, loader);

            assertThat(type.getField("TOKEN").get(null)).isEqualTo("secret");
        } finally {
            System.clearProperty(EagerConfig.REQUIRED_PROPERTY);
        }
    }

    private static URLClassLoader freshClassLoader() throws Exception {
        final String[] entries = System.getProperty("java.class.path").split(File.pathSeparator);
        final URL[] urls = new URL[entries.length];
        for (int i = 0; i < entries.length; i++) {
            urls[i] = Path.of(entries[i]).toUri().toURL();
        }
        // Parent = platform loader so this loader defines a fresh EagerConfig instead of delegating.
        return new URLClassLoader(urls, ClassLoader.getPlatformClassLoader());
    }
}
