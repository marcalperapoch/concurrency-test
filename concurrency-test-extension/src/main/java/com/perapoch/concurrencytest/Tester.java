package com.perapoch.concurrencytest;

import java.util.concurrent.CountDownLatch;
import java.util.function.IntConsumer;

public class Tester implements Runnable {

    private final IntConsumer action;
    private final CountDownLatch startLatch;
    private final CountDownLatch stopLatch;
    private final int timesToCallAction;

    Tester(final IntConsumer action,
           final CountDownLatch startLatch,
           final CountDownLatch stopLatch,
           final int timesToCallAction) {
        this.action = action;
        this.startLatch = startLatch;
        this.stopLatch = stopLatch;
        this.timesToCallAction = timesToCallAction;
    }


    @Override
    public void run() {
        try {
            startLatch.await();
            for (int i = 0; i < timesToCallAction; ++i) {
                action.accept(i);
            }
            stopLatch.countDown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
