package com.perapoch.concurrencytest;

public interface MeasuringMetersCalculator {

    void onNewDuration(String testName, long duration);

    MeasuringMeters get();

    void reset();
}
