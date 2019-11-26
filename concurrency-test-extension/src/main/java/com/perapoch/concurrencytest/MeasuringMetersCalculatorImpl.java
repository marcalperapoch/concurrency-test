package com.perapoch.concurrencytest;

public class MeasuringMetersCalculatorImpl implements MeasuringMetersCalculator {

    private String testName;
    private double avgTimeMs;
    private long maxTimeMs;
    private long minTimeMs;
    private int totalDurations;

    MeasuringMetersCalculatorImpl() {
        reset();
    }

    @Override
    public void onNewDuration(final String testName, final long duration) {
        this.testName = testName;
        this.avgTimeMs = ((avgTimeMs * totalDurations) + duration) / (totalDurations + 1);
        this.maxTimeMs = Math.max(maxTimeMs, duration);
        this.minTimeMs = Math.min(minTimeMs, duration);
        ++totalDurations;
    }

    @Override
    public MeasuringMeters get() {
        return new MeasuringMeters(testName, avgTimeMs, maxTimeMs, minTimeMs);
    }

    @Override
    public void reset() {
        totalDurations = 0;
        avgTimeMs = 0;
        minTimeMs = Long.MAX_VALUE;
        maxTimeMs = 0;
        testName = null;
    }
}
