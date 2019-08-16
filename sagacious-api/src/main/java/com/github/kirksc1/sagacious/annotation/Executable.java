package com.github.kirksc1.sagacious.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Executables.class)
public @interface Executable {
    public String scheme() default "";
}
