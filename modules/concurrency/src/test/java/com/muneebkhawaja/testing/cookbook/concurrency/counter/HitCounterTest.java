package com.muneebkhawaja.testing.cookbook.concurrency.counter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pastalab.fray.junit.junit5.FrayTestExtension;
import org.pastalab.fray.junit.junit5.annotations.ConcurrencyTest;

import com.muneebkhawaja.testing.cookbook.concurrency.support.ConcurrentTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(FrayTestExtension.class)
class HitCounterTest {
    private static final int THREAD_COUNT = 2;
    private static final int EXPECTED_HITS = THREAD_COUNT;

    @ConcurrencyTest(
            iterations = 1000
    )
    @DisplayName("Should remain consistent across multiple hits When the lock free counter is updated concurrently")
    @Test
    public void shouldRemainConsistentAcrossMultipleHitsWhenTheLockFreeCounterIsUpdatedConcurrently() throws InterruptedException {
        hitCounterTest(new LockFreeHitCounter());
    }

    @ConcurrencyTest(
            iterations = 1000
    )
    @DisplayName("Should remain consistent across multiple hits When the counter is updated concurrently")
    @Test
    public void shouldRemainConsistentAcrossMultipleHitsWhenTheRWLockCounterIsUpdatedConcurrently() throws InterruptedException {
        hitCounterTest(new RWLockHitCounter());
    }

    @ConcurrencyTest(
            iterations = 1000
    )
    @DisplayName("Should remain consistent across multiple hits When the private lock counter is updated concurrently")
    @Test
    public void shouldRemainConsistentAcrossMultipleHitsWhenThePrivateLockCounterIsUpdatedConcurrently() throws InterruptedException {
        hitCounterTest(new PrivateMonitorLockHitCounter());
    }

    @ConcurrencyTest(
            iterations = 1000
    )
    @DisplayName("Should remain consistent across multiple hits When the reentrant lock counter is updated concurrently")
    @Test
    public void shouldRemainConsistentAcrossMultipleHitsWhenTheReentrantLockCounterIsUpdatedConcurrently() throws InterruptedException {
        hitCounterTest(new ReentrantLockHitCounter());
    }


    private void hitCounterTest(final HitCounter counter) throws InterruptedException {
        ConcurrentTestUtils.runConcurrently(THREAD_COUNT, counter::incrementHits);
        assertEquals(EXPECTED_HITS, counter.getHits());
    }

}