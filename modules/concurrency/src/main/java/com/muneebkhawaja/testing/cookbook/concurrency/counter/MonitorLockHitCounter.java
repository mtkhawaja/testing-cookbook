package com.muneebkhawaja.testing.cookbook.concurrency.counter;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@ThreadSafe
public class MonitorLockHitCounter implements HitCounter {
    @GuardedBy("this")
    private int hits;

    public MonitorLockHitCounter() {
        this.hits = 0;
    }

    @Override
    public synchronized int getHits() {
        return hits;
    }

    @Override
    public synchronized int incrementHits() {
        hits++;
        return hits;

    }

    @Override
    public synchronized int resetHits() {
        final int previousHits = hits;
        hits = 0;
        return previousHits;
    }
}
