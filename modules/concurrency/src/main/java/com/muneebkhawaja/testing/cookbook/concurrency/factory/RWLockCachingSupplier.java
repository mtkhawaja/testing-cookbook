package com.muneebkhawaja.testing.cookbook.concurrency.factory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

@ThreadSafe
public class RWLockCachingSupplier<T> implements CachingSupplier<T> {
    private final ReentrantReadWriteLock lock;
    private final Supplier<T> delegate;
    @GuardedBy("lock")
    private T instance;

    public RWLockCachingSupplier(final Supplier<T> delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null!");
        this.lock = new ReentrantReadWriteLock();
        this.instance = null;
    }

    @Override
    public @NonNull T get() { // horrible idea, but why not?
        this.lock.readLock().lock();
        try {
            if (instance != null) {
                return instance;
            }
        } finally {
            lock.readLock().unlock();
        }
        this.lock.writeLock().lock();
        try {
            if (instance == null) {
                this.instance = Objects.requireNonNull(delegate.get(), "Delegate returned null");
            }
            return instance;
        } finally {
            lock.writeLock().unlock();
        }

    }
}
