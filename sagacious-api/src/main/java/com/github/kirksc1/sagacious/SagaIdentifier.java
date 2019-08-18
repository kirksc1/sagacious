package com.github.kirksc1.sagacious;

import org.springframework.util.Assert;

/**
 * The SagaIdentifier is a unique identifier for a saga. It represents a single instance of a saga.
 */
public final class SagaIdentifier {

    private final String identifier;

    /**
     * Construct a new SagaIdentifier.
     * @param identifier The unique identifier for the saga.
     */
    public SagaIdentifier(String identifier) {
        Assert.notNull(identifier, "The identifier provided is null");

        this.identifier = identifier;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return identifier;
    }
}
