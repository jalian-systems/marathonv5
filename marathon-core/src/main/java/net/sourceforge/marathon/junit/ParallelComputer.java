/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.junit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.runner.Computer;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;

import net.sourceforge.marathon.runtime.api.Constants;

public class ParallelComputer extends Computer {

    public static final Logger LOGGER = Logger.getLogger(ParallelComputer.class.getName());

    private final boolean fClasses;

    private final boolean fMethods;

    public ParallelComputer(boolean classes, boolean methods) {
        fClasses = classes;
        fMethods = methods;
    }

    public static Computer classes() {
        return new ParallelComputer(true, false);
    }

    public static Computer methods() {
        return new ParallelComputer(false, true);
    }

    private static Runner parallelize(Runner runner) {
        int nThreads = Integer.getInteger(Constants.NTHREADS, Runtime.getRuntime().availableProcessors());
        LOGGER.info("Using " + nThreads + " threads.");
        if (runner instanceof ParentRunner) {
            ((ParentRunner<?>) runner).setScheduler(new RunnerScheduler() {
                private final ExecutorService fService = Executors.newFixedThreadPool(nThreads);

                @Override
                public void schedule(Runnable childStatement) {
                    fService.submit(childStatement);
                }

                @Override
                public void finished() {
                    try {
                        fService.shutdown();
                        fService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                    }
                }
            });
        }
        return runner;
    }

    @Override
    public Runner getSuite(RunnerBuilder builder, java.lang.Class<?>[] classes) throws InitializationError {
        Runner suite = super.getSuite(builder, classes);
        return fClasses ? parallelize(suite) : suite;
    }

    @Override
    protected Runner getRunner(RunnerBuilder builder, Class<?> testClass) throws Throwable {
        Runner runner = super.getRunner(builder, testClass);
        return fMethods ? parallelize(runner) : runner;
    }
}
