package com.github.kirksc1.sagacious.server.valid;

import org.springframework.util.Assert;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ValidationException encapsulates validation errors within an RuntimeException.
 */
public class ValidationException extends RuntimeException {
    private List<ObjectError> errors = new ArrayList<>();

    /**
     * Construct a ValidationException with the provided errors.
     * @param errors A list of Spring validation errors.
     */
    public ValidationException(List<ObjectError> errors) {
        super(buildErrorMessage(errors));
        Assert.notNull(errors, "The list of ObjectErrors is null");
        this.errors.addAll(errors);
    }

    /**
     * Read the list of Spring validation errors for the ValidationException.
     * @return A list of Spring validation errors.
     */
    public List<ObjectError> getErrors() {
        return errors;
    }

    /**
     * Construct the message for the Exception.
     * @param errors A list of Spring validation errors.
     * @return The exception message.
     */
    private static String buildErrorMessage(List<ObjectError> errors) {
        List<ObjectError> tempErrors = Optional.ofNullable(errors).orElse(new ArrayList<>());
        return String.valueOf(tempErrors.size()) + " validation errors occurred";
    }
}
