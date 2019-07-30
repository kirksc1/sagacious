package com.github.kirksc1.sagacious;

import org.springframework.core.NamedThreadLocal;

public abstract class SagaContextHolder {

    private static final ThreadLocal<SagaContext> sagaContextHolder = new NamedThreadLocal<>("Saga context");

    public static void resetSagaContext() {
        sagaContextHolder.remove();
    }

    public static SagaContext getSagaContext() {
        return sagaContextHolder.get();
    }

    public static void setSagaContext(SagaContext sagaContext) {
        if (sagaContext == null) {
            resetSagaContext();
        } else {
            sagaContextHolder.set(sagaContext);
        }
    }
}
