package com.github.kirksc1.sagacious.annotation;

import org.springframework.util.Assert;

/**
 * ExceptionRule defines a mapping between a matching criteria (name) for Throwables and an indicator whether
 * a match with the criteria should result in a Saga failure.
 */
public final class ExceptionRule {

    /**
     * Depth indicator that no match was found.
     */
    public static final int NO_MATCH_DEPTH = -1;

    private final String exceptionName;
    private final boolean failOn;

    /**
     * Construct a new ExceptionRule with the provided class and failure indicator.
     * @param cls The Class that should be matched to a Throwable occurrence. Must be a Throwable.
     * @param failOn An indicator of whether a match should result in a Saga failure.
     */
    public ExceptionRule(Class<?> cls, boolean failOn) {
        Assert.notNull(cls, "The class provided is null");
        if (!Throwable.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("The class provided (" + cls.getName() +  ") is not a Throwable");
        }
        this.exceptionName = cls.getName();
        this.failOn = failOn;
    }

    /**
     * Construct a new ExceptionRule with the provided class name (or partial class name) and failure indicator.
     * @param clsName A portion of a name for a class that should be matched to a Throwable occurrence.
     * @param failOn An indicator of whether a match should result in a Saga failure.
     */
    public ExceptionRule(String clsName, boolean failOn) {
        Assert.notNull(clsName, "The class name provided is null");
        this.exceptionName = clsName;
        this.failOn = failOn;
    }

    /**
     * Indicate whether a match on the Throwable should result in a Saga failure.
     * @return True if the Saga should fail, otherwise false.
     */
    public boolean failOn() {
        return failOn;
    }

    /**
     * Retrieve the depth of the class matching.
     * @param e The Throwable to check for a match.
     * @return If matched, the depth found, otherwise NO_MATCH_DEPTH (-1).
     */
    public int getMatchDepth(Throwable e) {
        return getMatchDepth(e.getClass(), 0);
    }

    /**
     * Recursively check super classes of the exception class and identify the depth of any match.
     * @param exceptionClass The class of the Throwable to check.
     * @param depth The current depth of the check.
     * @return If matched, the depth found, otherwise NO_MATCH_DEPTH (-1).
     */
    private int getMatchDepth(Class<?> exceptionClass, int depth) {
        if (exceptionClass.getName().contains(this.exceptionName)) {
            return depth;
        }

        if (exceptionClass == Throwable.class) {
            //give up
            return NO_MATCH_DEPTH;
        }
        return getMatchDepth(exceptionClass.getSuperclass(), depth + 1);
    }
}
