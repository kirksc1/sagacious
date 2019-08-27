package com.github.kirksc1.sagacious.annotation;

/**
 * DefaultExceptionManager provides a concrete implementation of {@link ExceptionManager} that
 * fails on RuntimeExceptions and Errors.
 */
public class DefaultExceptionManager implements ExceptionManager {

    /**
     * {@inheritDoc}
     * <p>Failure occurs on RuntimeException OR Error</p>
     */
    @Override
    public boolean failOn(Throwable e) {
        return (e instanceof RuntimeException || e instanceof Error);
    }
}
