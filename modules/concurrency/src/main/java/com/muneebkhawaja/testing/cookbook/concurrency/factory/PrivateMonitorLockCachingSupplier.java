package com.muneebkhawaja.testing.cookbook.concurrency.factory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Supplier;

@ThreadSafe
public class PrivateMonitorLockCachingSupplier<T> implements CachingSupplier<T> {
    private final Object lock;
    private final Supplier<T> delegate;
    @GuardedBy("lock")
    private T instance;

    public PrivateMonitorLockCachingSupplier(final Supplier<T> delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null!");
        this.lock = new Object();
        this.instance = null;
    }

    @Override
    public @NonNull T get() {
        synchronized (lock) {
            if (instance == null) {
                instance = Objects.requireNonNull(delegate.get(), "Delegate returned null");
            }
            return instance;
        }
    }
}
