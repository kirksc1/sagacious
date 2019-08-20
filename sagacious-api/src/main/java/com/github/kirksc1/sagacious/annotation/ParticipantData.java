package com.github.kirksc1.sagacious.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a ParticipantData attribute on an individual method parameter.  It signifies that the parameter (within
 * a @{@link SagaParticipant} method) should be passed to the {@link com.github.kirksc1.sagacious.action.CompensatingActionDefinitionFactory}
 * to build the {@link com.github.kirksc1.sagacious.CompensatingActionDefinition} instance for the saga participant.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParticipantData {
}
