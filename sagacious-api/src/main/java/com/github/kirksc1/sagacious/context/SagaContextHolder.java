package com.github.kirksc1.sagacious.context;

import org.springframework.core.NamedThreadLocal;

/**
 * Holder class to expose the saga information in the form of a thread-bound {@link SagaContext} object.
 */
public abstract class SagaContextHolder {

    private static final ThreadLocal<SagaContext> sagaContextHolderLocal = new NamedThreadLocal<>("Saga context");

    //Prevent instantiation
    private SagaContextHolder() {
    }

    /**
     * Clear the SagaContext from the current thread.
     */
    public static void resetSagaContext() {
        sagaContextHolderLocal.remove();
    }

    /**
     * Retrieve the SagaContext for the current thread.
     * @return The SagaContext for the current thread.
     */
    public static SagaContext getSagaContext() {
        return sagaContextHolderLocal.get();
    }

    /**
     * Set the SagaContext for the current thread.
     * @param sagaContext The SagaContext.
     */
    public static void setSagaContext(SagaContext sagaContext) {
        if (sagaContext == null) {
            resetSagaContext();
        } else {
            sagaContextHolderLocal.set(sagaContext);
        }
    }
}
