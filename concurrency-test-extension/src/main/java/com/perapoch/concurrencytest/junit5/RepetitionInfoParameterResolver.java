package com.perapoch.concurrencytest.junit5;

import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.util.ToStringBuilder;

// Copied from org.junit.jupiter.engine.extension.RepetitionInfoParameterResolver

class RepetitionInfoParameterResolver implements ParameterResolver {

    private final int currentRepetition;
    private final int totalRepetitions;

    public RepetitionInfoParameterResolver(int currentRepetition, int totalRepetitions) {
        this.currentRepetition = currentRepetition;
        this.totalRepetitions = totalRepetitions;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return (parameterContext.getParameter().getType() == RepetitionInfo.class);
    }

    @Override
    public RepetitionInfo resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return new DefaultRepetitionInfo(this.currentRepetition, this.totalRepetitions);
    }

    private static class DefaultRepetitionInfo implements RepetitionInfo {

        private final int currentRepetition;
        private final int totalRepetitions;

        DefaultRepetitionInfo(int currentRepetition, int totalRepetitions) {
            this.currentRepetition = currentRepetition;
            this.totalRepetitions = totalRepetitions;
        }

        @Override
        public int getCurrentRepetition() {
            return this.currentRepetition;
        }

        @Override
        public int getTotalRepetitions() {
            return this.totalRepetitions;
        }

        @Override
        public String toString() {
            // @formatter:off
            return new ToStringBuilder(this)
                .append("currentRepetition", this.currentRepetition)
                .append("totalRepetitions", this.totalRepetitions)
                .toString();
            // @formatter:on
        }

    }
}

