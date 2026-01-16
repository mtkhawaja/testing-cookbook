package com.muneebkhawaja.testing.cookbook.concurrency.factory;

import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

///
/// Implementations must return the same instance for later calls after initialization
/// Implementations must also guarantee that initialization only hapens once.
///
public interface CachingSupplier<T> extends Supplier<T> {
    @Override
    @NonNull T get();

    static <T> CachingSupplier<T> of(final Supplier<T> delegate) {
        return new DoubleCheckedLockingCachingSupplier<>(delegate);
    }
}
