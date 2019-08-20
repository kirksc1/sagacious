package com.github.kirksc1.sagacious.context;

import com.github.kirksc1.sagacious.SagaIdentifier;
import com.github.kirksc1.sagacious.SagaManager;
import org.springframework.util.Assert;

/**
 * SagaContext provides a context for storing saga related information within the Thread via {@link SagaContextHolder}.
 */
public final class SagaContext {

    private final SagaManager sagaManager;
    private final SagaIdentifier identifier;

    /**
     * Construct a new SagaContext with the details provided.
     * @param sagaManager The SagaManager executing the current saga.
     * @param identifier The identifier for the current saga.
     */
    public SagaContext(SagaManager sagaManager, SagaIdentifier identifier) {
        Assert.notNull(sagaManager, "The SagaManager provided is null");
        Assert.notNull(identifier, "The identifier provided is null");

        this.sagaManager = sagaManager;
        this.identifier = identifier;
    }

    /**
     * Retrieve the SagaManager executing the current saga.
     * @return The SagaManager executing the current saga.
     */
    public SagaManager getSagaManager() {
        return sagaManager;
    }

    /**
     * Retrieve the identifier for the current saga.
     * @return The identifier for the current saga.
     */
    public SagaIdentifier getIdentifier() {
        return identifier;
    }
}
