package com.muneebkhawaja.testing.cookbook.concurrency.counter;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Scope("prototype")
@ThreadSafe
public class RWLockHitCounter implements HitCounter {
    private final ReadWriteLock lock;
    @GuardedBy("lock")
    private int hits;

    public RWLockHitCounter() {
        this.lock = new ReentrantReadWriteLock();
        this.hits = 0;
    }

    @Override
    public int getHits() {
        lock.readLock().lock();
        try {
            return hits;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int incrementHits() {
        lock.writeLock().lock();
        try {
            hits++;
            return hits;
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public int resetHits() {
        lock.writeLock().lock();
        try {
            final int previousHits = hits;
            hits = 0;
            return previousHits;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
