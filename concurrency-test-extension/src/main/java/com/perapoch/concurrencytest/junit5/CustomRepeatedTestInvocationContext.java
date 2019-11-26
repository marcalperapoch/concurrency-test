package com.perapoch.concurrencytest.junit5;

import com.perapoch.concurrencytest.ConcurrencyTest;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

import java.util.List;

import static java.util.Collections.singletonList;

// Inspired by org.junit.jupiter.engine.extension.RepeatedTestInvocationContext

public class CustomRepeatedTestInvocationContext implements TestTemplateInvocationContext {


    private static final String PATTERN = String.format("%s - %s/%s",
                                                        ConcurrencyTest.DISPLAY_NAME_PLACEHOLDER,
                                                        ConcurrencyTest.CURRENT_REPETITION_PLACEHOLDER,
                                                        ConcurrencyTest.TOTAL_REPETITIONS_PLACEHOLDER);

    private final int currentRepetition;
    private final int totalRepetitions;
    private final String displayName;

    public CustomRepeatedTestInvocationContext(int currentRepetition,
                                               int totalRepetitions,
                                               String displayName) {

        this.currentRepetition = currentRepetition;
        this.totalRepetitions = totalRepetitions;
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return PATTERN.replace(ConcurrencyTest.DISPLAY_NAME_PLACEHOLDER, displayName)
                      .replace(ConcurrencyTest.CURRENT_REPETITION_PLACEHOLDER, String.valueOf(currentRepetition))
                      .replace(ConcurrencyTest.TOTAL_REPETITIONS_PLACEHOLDER, String.valueOf(totalRepetitions));
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return singletonList(new RepetitionInfoParameterResolver(currentRepetition, totalRepetitions));
    }

}
