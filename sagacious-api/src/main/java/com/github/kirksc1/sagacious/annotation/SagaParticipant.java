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

    /**
     * Defines zero (0) or more exception {@link Class classes}, which must be
     * subclasses of {@link Throwable}, indicating which exception types must cause
     * a saga failure.
     * <p>By default, a saga will be rolling back on {@link RuntimeException}
     * and {@link Error} but not on checked exceptions (business exceptions).
     * <p>This is the preferred way to construct a rollback rule (in contrast to
     * {@link #failForClassName}), matching the exception class and its subclasses.
     * @see #failForClassName
     * @return An array of Classes that, when matched, should fail the Saga.
     */
    Class<? extends Throwable>[] failFor() default {};

    /**
     * Defines zero (0) or more exception names (for exceptions which must be a
     * subclass of {@link Throwable}), indicating which exception types must cause
     * a saga failure.
     * <p>This can be a substring of a fully qualified class name, with no wildcard
     * support at present. For example, a value of {@code "ServletException"} would
     * match {@code javax.servlet.ServletException} and its subclasses.
     * <p><b>NB:</b> Consider carefully how specific the pattern is and whether
     * to include package information (which isn't mandatory). For example,
     * {@code "Exception"} will match nearly anything and will probably hide other
     * rules. {@code "java.lang.Exception"} would be correct if {@code "Exception"}
     * were meant to define a rule for all checked exceptions. With more unusual
     * {@link Exception} names such as {@code "BaseBusinessException"} there is no
     * need to use a FQN.
     * @see #failFor
     * @return An array of Class names (or partial class names) that, when matched,
     * should fail the Saga.
     */
    String[] failForClassName() default {};

    /**
     * Defines zero (0) or more exception {@link Class Classes}, which must be
     * subclasses of {@link Throwable}, indicating which exception types must
     * <b>not</b> cause a saga failure.
     * <p>This is the preferred way to construct a rollback rule (in contrast
     * to {@link #noFailForClassName}), matching the exception class and
     * its subclasses.
     * @see #noFailForClassName
     * @return An array of Classes that, when matched, should NOT fail the Saga.
     */
    Class<? extends Throwable>[] noFailFor() default {};

    /**
     * Defines zero (0) or more exception names (for exceptions which must be a
     * subclass of {@link Throwable}) indicating which exception types must <b>not</b>
     * cause a saga failure.
     * <p>See the description of {@link #failForClassName} for further
     * information on how the specified names are treated.
     * @see #noFailFor
     * @return An array of Class names (or partial class names) that, when matched,
     * should NOT fail the Saga.
     */
    String[] noFailForClassName() default {};

    /**
     * Defines whether the participant processing will fail a Saga according to the
     * failForXYZ or noFailForXYZ logic.
     * @return True if the participant processing can fail the Saga, otherwise false.
     */
    boolean autoFail() default true;
}
