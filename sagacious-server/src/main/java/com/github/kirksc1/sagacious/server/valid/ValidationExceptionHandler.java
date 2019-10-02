package com.github.kirksc1.sagacious.server.valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ValidationExceptionHandler provides exception handling for ValidationExceptions ensuring
 * the response is properly formatted.
 */
@ControllerAdvice
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle the provide ValidationException.
     * @param ex The ValidationException.
     * @return The response.
     */
    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<List<ErrorResource>> handleValidationException(ValidationException ex) {
        List<ErrorResource> responseErrors = ex.getErrors().stream()
                .map(ValidationExceptionHandler::toErrorResource)
                .collect(Collectors.toList());

        return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Convert a Spring validation error into an ErrorResource.
     * @param error The Spring validation error.
     * @return The ErrorResource.
     */
    private static ErrorResource toErrorResource(ObjectError error) {
        ErrorResource retVal = new ErrorResource();

        Optional.ofNullable(error)
                .filter(e1 -> error instanceof FieldError)
                .map(e2 -> (FieldError)e2)
                .ifPresent(fieldError1 -> {
                    retVal.setScope(fieldError1.getField());
                    retVal.setCode(convertCode(fieldError1.getCode()));
                });

        return retVal;
    }

    /**
     * Convert the provided Spring validation code to a custom code.
     * @param code The Spring validation code.
     * @return The Sagacious code.
     */
    private static String convertCode(String code) {
        String retVal = null;
        if (code.equals("NotNull")) {
            retVal = "field.required";
        }
        return retVal;
    }
}