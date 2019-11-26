package com.perapoch.concurrencytest;

import java.util.function.Consumer;

public class ConcurrencyExtensionBuilder {

    private int numThreads;
    private int threadPoolSize;
    private int timesToCallAction;
    private int warmUpIterations;
    private int iterations;
    private Consumer<MeasuringMeters> outputConsumer;

    ConcurrencyExtensionBuilder() {
        this.outputConsumer = measuringMeters -> System.out.println(measuringMeters);
    }


    public ConcurrencyExtensionBuilder numThreads(final int numThreads) {
        this.numThreads = numThreads;
        return this;
    }

    public ConcurrencyExtensionBuilder threadPoolSize(final int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        return this;
    }

    public ConcurrencyExtensionBuilder timesToCallAction(final int timesToCallAction) {
        this.timesToCallAction = timesToCallAction;
        return this;
    }

    public TimeMeasuringBuilder withTimeMeasuring() {
        return new TimeMeasuringBuilder(this);
    }

    public ConcurrencyExtension build() {
        return new ConcurrencyExtension(numThreads,
                                        threadPoolSize,
                                        timesToCallAction,
                                        warmUpIterations,
                                        iterations,
                                        outputConsumer,
                                        new MeasuringMetersCalculatorImpl());
    }

    public static class TimeMeasuringBuilder {

        final ConcurrencyExtensionBuilder parent;

        private TimeMeasuringBuilder(final ConcurrencyExtensionBuilder parent) {
            this.parent = parent;
        }

        public TimeMeasuringBuilder warmUpIterations(final int warmUpIterations) {
            parent.warmUpIterations = warmUpIterations;
            return this;
        }

        public TimeMeasuringBuilder iterations(final int iterations) {
            parent.iterations = iterations;
            return this;
        }

        public TimeMeasuringBuilder withOutputConsumer(final Consumer<MeasuringMeters> outputConsumer) {
            parent.outputConsumer = outputConsumer;
            return this;
        }

        public ConcurrencyExtensionBuilder done() {
            return parent;
        }
    }
}
