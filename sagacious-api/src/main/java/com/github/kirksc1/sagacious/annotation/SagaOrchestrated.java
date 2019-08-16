package com.github.kirksc1.sagacious.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SagaOrchestrated {
    public String sagaManager() default "sagaManager";
    public String identifierFactory() default "sagaIdentifierFactory";
}
