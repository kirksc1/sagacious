package com.github.kirksc1.sagacious.annotation;

/**
 * ExceptionManager defines the responsibilities of classes that decide whether a Throwable should result
 * in a Saga failure.
 */
public interface ExceptionManager {

    /**
     * Determine if the provided Throwable should result in a Saga failure.
     * @param e The Throwable that has ocurred.
     * @return True if a failure should occur, otherwise false.
     */
    boolean failOn(Throwable e);
}
