package com.muneebkhawaja.testing.cookbook.unit.extension;

import java.util.Random;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/// A custom JUnit Jupiter extension demonstrating the two most common extension points:
///
/// - [BeforeEachCallback] — seeds a fresh, deterministic [Random] into the per-test [Store]
///   so values are reproducible across runs.
/// - [ParameterResolver] — injects an `int` into any test parameter annotated with [RandomInt],
///   drawn from the seeded `Random` within the annotation's `[min, max)` range.
public final class SeededRandomExtension implements BeforeEachCallback, ParameterResolver {

    /// Fixed so injected values are deterministic; tests can reference it to derive expected values.
    static final long SEED = 42L;

    private static final Namespace NAMESPACE = Namespace.create(SeededRandomExtension.class);
    private static final String RANDOM_KEY = "random";

    @Override
    public void beforeEach(final ExtensionContext context) {
        context.getStore(NAMESPACE).put(RANDOM_KEY, new Random(SEED));
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(RandomInt.class)
                && parameterContext.getParameter().getType() == int.class;
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext) {
        final RandomInt spec = parameterContext.findAnnotation(RandomInt.class)
                .orElseThrow(() -> new ParameterResolutionException("@RandomInt is required"));
        if (spec.min() >= spec.max()) {
            throw new ParameterResolutionException(
                    "@RandomInt requires min < max, got min=%d max=%d".formatted(spec.min(), spec.max()));
        }
        final Random random = extensionContext.getStore(NAMESPACE).get(RANDOM_KEY, Random.class);
        return random.nextInt(spec.min(), spec.max());
    }
}
