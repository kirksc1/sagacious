package com.github.kirksc1.sagacious.annotation;

import java.lang.annotation.*;

/**
 * Describes an Executable attribute on an individual class.  It is intended to be used on {@link com.github.kirksc1.sagacious.action.CompensatingActionExecutor}
 * classes, where it defines the {@link com.github.kirksc1.sagacious.CompensatingActionDefinition} instances that the
 * CompensatingActionExecutor can successfully execute.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Executables.class)
public @interface Executable {

    /**
     * Defines a URI scheme that the {@link com.github.kirksc1.sagacious.action.CompensatingActionExecutor} is
     * capable of executing.
     * @return The URI scheme.
     */
    public String scheme() default "";
}
