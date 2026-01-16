package com.muneebkhawaja.testing.cookbook.concurrency.counter;

import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Primary
@Component
@Scope("prototype")
@ThreadSafe
public class LockFreeHitCounter implements HitCounter {
    private final AtomicInteger hits;

    public LockFreeHitCounter() {
        this.hits = new AtomicInteger(0);
    }

    @Override
    public int getHits() {
        return hits.get();
    }

    @Override
    public int incrementHits() {
        return hits.incrementAndGet();
    }

    @Override
    public int resetHits() {
        return hits.getAndSet(0);
    }
}
