package com.github.kirksc1.sagacious.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a SagaParticipant attribute on an individual method.  It signifies that the method should be executed
 * as a participant in the current saga, if present.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SagaParticipant {

    /**
     * Defines the name of the bean (implementing {@link com.github.kirksc1.sagacious.action.CompensatingActionDefinitionFactory})
     * to be used in building the {@link com.github.kirksc1.sagacious.CompensatingActionDefinition} instance for the
     * saga participant.
     * @return The name of the CompensatingActionDefinitionFactory bean.
     */
    public String actionDefinitionFactory();

    /**
     * Defines the name of the bean (implementing {@link IdentifierFactory}) that is to be used to generate a
     * unique identifier for the saga participant.
     * <p>Defaults to {@code "participantIdentifierFactory"}.
     * @return The name of the IdentifierFactory bean.
     */
    public String identifierFactory() default "participantIdentifierFactory";
}
