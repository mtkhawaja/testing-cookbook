package com.muneebkhawaja.testing.cookbook.concurrency.counter;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@ThreadSafe
public class PrivateMonitorLockHitCounter implements HitCounter {
    private final Object lock;
    @GuardedBy("lock")
    private int hits;

    public PrivateMonitorLockHitCounter() {
        this.lock = new Object();
        this.hits = 0;
    }

    @Override
    public int getHits() {
        synchronized (this.lock) {
            return hits;
        }
    }

    @Override
    public int incrementHits() {
        synchronized (this.lock) {
            hits++;
            return hits;

        }
    }


    @Override
    public int resetHits() {
        synchronized (this.lock) {
            final int previousHits = hits;
            hits = 0;
            return previousHits;
        }

    }
}
