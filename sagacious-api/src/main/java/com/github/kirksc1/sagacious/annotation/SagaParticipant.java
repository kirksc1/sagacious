package com.github.kirksc1.sagacious.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SagaParticipant {
    public String actionDefinitionFactory();
    public String identifierFactory() default "participantIdentifierFactory";
}