package com.github.kirksc1.sagacious.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides a container for multiple {@link Executable} annotations.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Executables {

    /**
     * Retrieve the {@link Executable} annotations.
     * @return The {@link Executable} annotations.
     */
    public Executable[] value();
}
