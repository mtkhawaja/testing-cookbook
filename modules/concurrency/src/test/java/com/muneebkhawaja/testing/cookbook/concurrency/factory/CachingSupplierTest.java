package com.muneebkhawaja.testing.cookbook.concurrency.factory;

import com.muneebkhawaja.testing.cookbook.concurrency.support.ConcurrentTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pastalab.fray.junit.junit5.FrayTestExtension;
import org.pastalab.fray.junit.junit5.annotations.ConcurrencyTest;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(FrayTestExtension.class)
class CachingSupplierTest {
    @ConcurrencyTest(
            iterations = 1000
    )
    @DisplayName("Should initialize only once atomically When the DoubleCheckedLockingCachingSupplier  is invoked by multiple threads")
    @Test
    public void shouldInitializeOnlyOnceAtomicallyWhenTheDoubleCheckedLockingCachingSupplierIsInvokedByMultipleThreads() throws InterruptedException {
        cachingSupplierTest(DoubleCheckedLockingCachingSupplier::new);
    }

    @ConcurrencyTest(
            iterations = 1000
    )
    @DisplayName("Should initialize only once atomically When the MonitorLockCachingSupplier is invoked by multiple threads")
    @Test
    public void shouldInitializeOnlyOnceAtomicallyWhenTheMonitorLockCachingSupplierIsInvokedByMultipleThreads() throws InterruptedException {
        cachingSupplierTest(MonitorLockCachingSupplier::new);
    }

    @ConcurrencyTest(
            iterations = 1000
    )
    @DisplayName("Should initialize only once atomically When the PrivateMonitorLockCachingSupplier is invoked by multiple threads")
    @Test
    public void shouldInitializeOnlyOnceAtomicallyWhenThePrivateMonitorLockCachingSupplierIsInvokedByMultipleThreads() throws InterruptedException {
        cachingSupplierTest(PrivateMonitorLockCachingSupplier::new);
    }

    @ConcurrencyTest(
            iterations = 1000
    )
    @DisplayName("Should initialize only once atomically When the RWLockCachingSupplier is invoked by multiple threads")
    @Test
    public void shouldInitializeOnlyOnceAtomicallyWhenTheRWLockCachingSupplierIsInvokedByMultipleThreads() throws InterruptedException {
        cachingSupplierTest(RWLockCachingSupplier::new);
    }

    @ConcurrencyTest(
            iterations = 1000
    )
    @DisplayName("Should initialize only once atomically When the AtomicSafeInitializerCachingSupplier is invoked by multiple threads")
    @Test
    public void shouldInitializeOnlyOnceAtomicallyWhenTheAtomicSafeInitializerCachingSupplierIsInvokedByMultipleThreads() throws InterruptedException {
        cachingSupplierTest(AtomicSafeInitializerCachingSupplier::new);
    }


    private void cachingSupplierTest(
            final Function<Supplier<Object>, CachingSupplier<Object>> cachingSupplierFactory
    ) throws InterruptedException {
        final AtomicInteger initializationCount = new AtomicInteger();
        final Supplier<Object> delegate = () -> {
            initializationCount.incrementAndGet();
            return new Object();
        };
        final CachingSupplier<Object> cachingSupplier = cachingSupplierFactory.apply(delegate);
        final Set<Object> values = ConcurrentHashMap.newKeySet();
        ConcurrentTestUtils.runConcurrently(() -> values.add(cachingSupplier.get()));
        assertEquals(1, initializationCount.get());
        assertEquals(1, values.size());
        final Object firstValue = values.iterator().next();
        assertSame(firstValue, cachingSupplier.get());
    }
}