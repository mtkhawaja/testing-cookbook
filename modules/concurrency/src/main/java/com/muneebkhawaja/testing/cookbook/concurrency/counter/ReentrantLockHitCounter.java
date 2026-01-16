package com.muneebkhawaja.testing.cookbook.concurrency.counter;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Scope("prototype")
@ThreadSafe
public class ReentrantLockHitCounter implements HitCounter {
    private final ReentrantLock lock;
    @GuardedBy("lock")
    private int hits;

    public ReentrantLockHitCounter() {
        this.lock = new ReentrantLock();
        this.hits = 0;
    }

    @Override
    public int getHits() {
        lock.lock();
        try {
            return hits;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int incrementHits() {
        lock.lock();
        try {
            hits++;
            return hits;
        } finally {
            lock.unlock();
        }

    }

    @Override
    public int resetHits() {
        lock.lock();
        try {
            final int previousHits = hits;
            hits = 0;
            return previousHits;
        } finally {
            lock.unlock();
        }
    }
}
