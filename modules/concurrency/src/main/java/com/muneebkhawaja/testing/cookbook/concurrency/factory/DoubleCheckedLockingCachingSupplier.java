package com.muneebkhawaja.testing.cookbook.concurrency.factory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Supplier;

///
/// Basically copied as is from Effective Java, 3rd Edition, Pg. 333
/// Item 83: Use initialization judiciously
///
@ThreadSafe
public class DoubleCheckedLockingCachingSupplier<T> implements CachingSupplier<T> {
    private final Supplier<T> delegate;
    @GuardedBy("this")
    private volatile T instance;

    public DoubleCheckedLockingCachingSupplier(final Supplier<T> delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null!");
        this.instance = null;
    }

    @Override
    public @NonNull T get() {
        // Ensures that 'instance' is 'read' only once in the common case
        final T result = instance;
        if (result != null) { // first check (no locking)
            return result;
        }
        synchronized (this) {
            if (instance == null) { // second check (with locking)
                instance = Objects.requireNonNull(delegate.get(), "Delegate returned null");
            }
            return instance;
        }
    }
}
