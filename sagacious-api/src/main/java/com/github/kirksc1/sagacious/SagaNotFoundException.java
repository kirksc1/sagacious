package com.github.kirksc1.sagacious;

/**
 * SagaNotFoundException represents an attempted operation on a Saga that could not be found.
 */
public class SagaNotFoundException extends RuntimeException {
    /**
     * Construct a new SagaNotFoundException for the provided identifier.
     * @param identifier The identifier for the Saga.
     */
    public SagaNotFoundException(SagaIdentifier identifier) {
        super(identifier.toString() + " could not be found");
    }
}
