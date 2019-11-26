package com.perapoch.concurrencytest;

import com.perapoch.concurrencytest.junit5.CustomRepeatedTestInvocationContext;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;

public class ConcurrencyExtension implements TestTemplateInvocationContextProvider, BeforeEachCallback, BeforeTestExecutionCallback, AfterAllCallback {

    private static final String START_TIME = "start_time";

    private final int numThreads;
    private final int timesToCallAction;
    private final int warmUpIterations;
    private final int iterations;
    private final int totalIterations;
    private final Consumer<MeasuringMeters> outputConsumer;

    private boolean isWarmUpDone;
    private boolean isLastIteration;

    private final ExecutorService executor;
    private final MeasuringMetersCalculator measuringMetersCalculator;

    private CountDownLatch startLatch;
    private CountDownLatch stopLatch;
    private ExtensionContext context;

    ConcurrencyExtension(final int numThreads,
                         final int threadPoolSize,
                         final int timesToCallAction,
                         final int warmUpIterations,
                         final int iterations,
                         final Consumer<MeasuringMeters> outputConsumer,
                         final MeasuringMetersCalculator measuringMetersCalculator) {
        this.numThreads = numThreads;
        this.timesToCallAction = timesToCallAction;
        this.warmUpIterations = warmUpIterations;
        this.iterations = iterations;
        this.totalIterations = warmUpIterations + iterations;
        this.outputConsumer = outputConsumer;
        this.measuringMetersCalculator = measuringMetersCalculator;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        resetMeasuring();
    }

    private void resetMeasuring() {
        isLastIteration = false;
        isWarmUpDone = false;
        measuringMetersCalculator.reset();
    }

    @Override
    public void beforeTestExecution(final ExtensionContext context) throws Exception {
        this.context = context;
        getStore(context).put(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        startLatch = new CountDownLatch(1);
        stopLatch = new CountDownLatch(numThreads);
    }

    @Override
    public void afterAll(final ExtensionContext context) throws Exception {
        executor.shutdown();
    }

    @Override
    public boolean supportsTestTemplate(final ExtensionContext context) {
        if (!context.getTestMethod().isPresent()) {
            return false;
        }

        final Method testMethod = context.getTestMethod().get();
        if (!isAnnotated(testMethod, ConcurrencyTest.class)) {
            return false;
        }

        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(final ExtensionContext context) {
        String displayName = context.getDisplayName();
        Spliterator<TestTemplateInvocationContext> spliterator =
                spliteratorUnknownSize(new TestTemplateIteratorParams(displayName), Spliterator.NONNULL);
        return stream(spliterator, false);
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }

    public void runAndWaitFor(final IntConsumer action) {
        try {
            IntStream.range(0, numThreads)
                    .mapToObj(i -> new Tester(action, startLatch, stopLatch, timesToCallAction))
                    .forEach(executor::submit);

            startLatch.countDown();
            stopLatch.await();

            if (isWarmUpDone) {
                Method testMethod = context.getRequiredTestMethod();
                long startTime = getStore(context).remove(START_TIME, long.class);
                long duration = System.currentTimeMillis() - startTime;
                measuringMetersCalculator.onNewDuration(testMethod.getName(), duration);
                if (isLastIteration) {
                    if (outputConsumer != null) {
                        outputConsumer.accept(measuringMetersCalculator.get());
                    }
                    resetMeasuring();
                }
            }
        } catch (Exception e) {
            fail(e);
        }
    }

    public static ConcurrencyExtensionBuilder builder() {
        return new ConcurrencyExtensionBuilder();
    }

    class TestTemplateIteratorParams implements Iterator<CustomRepeatedTestInvocationContext> {

        private final String displayName;
        private int repetition;

        TestTemplateIteratorParams(String displayName) {
            this.displayName = displayName;
            this.repetition = 0;
        }

        @Override
        public boolean hasNext() {
            return repetition < warmUpIterations + iterations;
        }

        @Override
        public CustomRepeatedTestInvocationContext next() {
            ++repetition;

            if (repetition == warmUpIterations) {
                isWarmUpDone = true;
            } else if (repetition == totalIterations) {
                isLastIteration = true;
            }
            return new CustomRepeatedTestInvocationContext(repetition, totalIterations, displayName);
        }
    }


}
