package com.github.kirksc1.sagacious.server.valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ValidConfig is a Spring Bean Configuration class that provides beans within the "valid" package.
 */
@Configuration
public class ValidConfig {

    /**
     * Default ValiationExceptionHandler bean.
     * @return A ValiationExceptionHandler.
     */
    @Bean
    @ConditionalOnProperty(value = "sagacious.server.exception-handling.enabled", havingValue = "true", matchIfMissing = true)
    public ValidationExceptionHandler validationExceptionHandler() {
        return new ValidationExceptionHandler();
    }
}
