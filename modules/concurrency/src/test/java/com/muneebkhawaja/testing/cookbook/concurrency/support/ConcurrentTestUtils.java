package com.muneebkhawaja.testing.cookbook.concurrency.support;

import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/// Concurrency test helper for performing an action concurrently.
/// The action runs once per thread, so it executes threadCount times in total.
///
/// ## Example
/// ```java
/// ConcurrentTestUtils.runConcurrently(2, counter::incrementHits);
/// ```
///
/// ## Custom executor
/// ```java
/// try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
///     ConcurrentTestUtils.runConcurrently(executor, 4, counter::incrementHits);
/// }
/// ```
@ThreadSafe
public final class ConcurrentTestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentTestUtils.class);

    private ConcurrentTestUtils() {
        throw new AssertionError(ConcurrentTestUtils.class + " is a static utility class and must not be instantiated.");
    }

    /// Runs the provided action concurrently using a fixed thread pool.
    /// The action runs once per thread.
    /// Defaults to the available processor count, with a minimum of 1 thread.
    ///
    /// @param action shared action to run concurrently
    /// @throws InterruptedException if the coordinating thread is interrupted
    /// @throws AssertionError       if any concurrent task fails
    public static void runConcurrently(
            final Runnable action
    ) throws InterruptedException {
        runConcurrently(defaultThreadCount(), action);
    }

    /// Runs the provided action concurrently using a fixed thread pool.
    /// The action runs once per thread.
    ///
    /// @param threadCount number of threads to start; must be at least 1
    /// @param action      shared action to run concurrently
    /// @throws InterruptedException if the coordinating thread is interrupted
    /// @throws AssertionError       if any concurrent task fails
    public static void runConcurrently(
            final int threadCount,
            final Runnable action
    ) throws InterruptedException {
        LOGGER.trace("Starting concurrent execution with fixed thread pool of size '{}' threads.", threadCount);
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            runConcurrently(executor, threadCount, action);
        }
    }

    /// Runs the provided action concurrently across the requested number of threads.
    /// The action runs once per thread.
    ///
    /// ## Notes
    /// - All thrown exceptions are collected and rethrown as a single AssertionError.
    ///
    /// @param threadCount number of threads to start; must be at least 1
    /// @param action      shared action to run concurrently
    /// @throws InterruptedException if the coordinating thread is interrupted
    /// @throws AssertionError       if any concurrent task fails
    public static void runConcurrently(
            final ExecutorService executor,
            final int threadCount,
            final Runnable action
    ) throws InterruptedException {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Thread count must be at least 1.");
        }
        Objects.requireNonNull(executor, "Executor must not be null.");
        Objects.requireNonNull(action, "Action must not be null.");
        final CoordinationLatches latches = new CoordinationLatches(threadCount);
        final Queue<Throwable> failures = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    LOGGER.trace("Thread Ready.");
                    latches.ready().countDown();
                    LOGGER.trace("Waiting for start signal.");
                    latches.start().await();
                    LOGGER.debug("Concurrent action starting.");
                    action.run();
                } catch (final InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    LOGGER.warn("Concurrent action interrupted.", interruptedException);
                    failures.add(interruptedException);
                } catch (final Exception exception) {
                    LOGGER.error("Concurrent action failed.", exception);
                    failures.add(exception);
                } finally {
                    LOGGER.debug("Concurrent action finished.");
                    latches.done().countDown();
                }
            });
        }
        latches.ready().await();
        latches.start().countDown();
        latches.done().await();

        if (!failures.isEmpty()) {
            AssertionError error = new AssertionError("Concurrent execution failed.");
            failures.forEach(error::addSuppressed);
            throw error;
        }
    }

    private record CoordinationLatches(CountDownLatch ready, CountDownLatch start, CountDownLatch done) {
        private CoordinationLatches(int threadCount) {
            this(new CountDownLatch(threadCount), new CountDownLatch(1), new CountDownLatch(threadCount));
        }
    }

    private static int defaultThreadCount() {
        return Math.max(1, Runtime.getRuntime().availableProcessors());
    }
}
