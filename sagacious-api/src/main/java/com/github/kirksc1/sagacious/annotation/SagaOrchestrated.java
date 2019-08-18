package com.github.kirksc1.sagacious.annotation;

import com.github.kirksc1.sagacious.SagaManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a SagaOrchestrated attribute on an individual method.  It signifies that the method should be orchestrated
 * as a saga.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SagaOrchestrated {

    /**
     * Defines the name of the bean (implementing {@link SagaManager}) that is to be used to manage the saga
     * lifecycle.
     * <p>Defaults to {@code "sagaManager"}.
     * @return The name of the SagaManager bean.
     */
    public String sagaManager() default "sagaManager";

    /**
     * Defines the name of the bean (implementing {@link IdentifierFactory}) that is to be used to generate a
     * unique identifier for the saga.
     * <p>Defaults to {@code "sagaIdentifierFactory"}.
     * @return The name of the IdentifierFactory bean.
     */
    public String identifierFactory() default "sagaIdentifierFactory";
}
