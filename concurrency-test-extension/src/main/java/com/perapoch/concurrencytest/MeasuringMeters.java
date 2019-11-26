package com.perapoch.concurrencytest;

import static java.lang.String.format;

public class MeasuringMeters {

    private static final String TO_STRING_PATTERN= "[%s] => Avg: %4.2f ms, Max: %4.2f ms, Min %4.2f ms";

    private final String methodName;
    private final double avgMs;
    private final double maxMs;
    private final double minMs;
    private final String precomputedToString;

    MeasuringMeters(String methodName, double avgMs, double maxMs, double minMs) {
        this.methodName = methodName;
        this.avgMs = avgMs;
        this.maxMs = maxMs;
        this.minMs = minMs;
        this.precomputedToString = format(TO_STRING_PATTERN, methodName, avgMs, maxMs, minMs);
    }

    public String getMethodName() {
        return methodName;
    }

    public double getAvgMs() {
        return avgMs;
    }

    public double getMaxMs() {
        return maxMs;
    }

    public double getMinMs() {
        return minMs;
    }

    @Override
    public String toString() {
        return precomputedToString;
    }
}
