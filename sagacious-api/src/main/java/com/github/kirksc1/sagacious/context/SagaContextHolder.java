package com.github.kirksc1.sagacious.context;

import org.springframework.core.NamedThreadLocal;

public abstract class SagaContextHolder {

    private static final ThreadLocal<SagaContext> sagaContextHolderLocal = new NamedThreadLocal<>("Saga context");

    private SagaContextHolder() {

    }

    public static void resetSagaContext() {
        sagaContextHolderLocal.remove();
    }

    public static SagaContext getSagaContext() {
        return sagaContextHolderLocal.get();
    }

    public static void setSagaContext(SagaContext sagaContext) {
        if (sagaContext == null) {
            resetSagaContext();
        } else {
            sagaContextHolderLocal.set(sagaContext);
        }
    }
}
