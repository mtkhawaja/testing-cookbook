package com.muneebkhawaja.testing.cookbook.concurrency.factory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Supplier;

@ThreadSafe
public class MonitorLockCachingSupplier<T> implements CachingSupplier<T> {
    private final Supplier<T> delegate;
    @GuardedBy("this")
    private T instance;

    public MonitorLockCachingSupplier(final Supplier<T> delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null!");
    }

    @Override
    public synchronized @NonNull T get() {
        if (instance == null) {
            instance = Objects.requireNonNull(delegate.get(), "Delegate returned null");
        }
        return instance;
    }
}
