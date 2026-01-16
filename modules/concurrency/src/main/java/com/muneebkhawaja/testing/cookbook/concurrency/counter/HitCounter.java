package com.muneebkhawaja.testing.cookbook.concurrency.counter;


public interface HitCounter {
    /**
     *
     * @return the current counter value
     */
    int getHits();

    /**
     *
     * @return increments the counter by one and returns the updated value
     */
    int incrementHits();

    /**
     *
     * @return resets the counter to zero and returns the previous value
     */
    int resetHits();
}
