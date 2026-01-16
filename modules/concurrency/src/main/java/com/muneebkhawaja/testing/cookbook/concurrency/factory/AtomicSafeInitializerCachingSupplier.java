package com.muneebkhawaja.testing.cookbook.concurrency.factory;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Supplier;

@ThreadSafe // And lock free!
public class AtomicSafeInitializerCachingSupplier<T> implements CachingSupplier<T> {
    private final AtomicSafeInitializer<T> atomicSafeInitializer;

    public AtomicSafeInitializerCachingSupplier(final Supplier<T> delegate) {
        Objects.requireNonNull(delegate, "delegate must not be null!");
        this.atomicSafeInitializer = AtomicSafeInitializer.<T>builder()
                .setInitializer(() -> Objects.requireNonNull(delegate.get(), "Delegate returned null"))
                .setCloser(_ -> {
                })
                .get();
    }

    @Override
    public @NonNull T get() {
        try {
            return atomicSafeInitializer.get();
        } catch (ConcurrentException e) {
            throw new RuntimeException(e);
        }
    }
}
