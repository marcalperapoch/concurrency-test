package com.perapoch.concurrencytest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AtomicIntegerTest {

    private static final int NUM_THREADS = 100;
    private static final int TIMES_PER_ACTION = 1000;

    @RegisterExtension
    static ConcurrencyExtension concurrency = ConcurrencyExtension.builder()
            .numThreads(NUM_THREADS)
            .threadPoolSize(10)
            .timesToCallAction(TIMES_PER_ACTION)
            .withTimeMeasuring()
                .warmUpIterations(4)
                .iterations(10)
                .withOutputConsumer(meters -> System.out.println(meters))
                .done()
            .build();


    // SUT
    private AtomicInteger counter;

    @BeforeEach
    public void init() {
        counter = new AtomicInteger();
    }

    @ConcurrencyTest
    public void incrementAndGet_shouldNotRace() {
        // When
        concurrency.runAndWaitFor(iteration -> counter.incrementAndGet());

        // Then
        assertEquals(NUM_THREADS * TIMES_PER_ACTION, counter.get());
    }
}
