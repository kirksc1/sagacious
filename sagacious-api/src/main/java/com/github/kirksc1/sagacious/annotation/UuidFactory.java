package com.github.kirksc1.sagacious.annotation;

import java.util.UUID;

/**
 * UuidFactory is a concrete implementation of the {@link IdentifierFactory} interface that uses the
 * Java UUID class to generate random identifiers.
 */
public class UuidFactory implements IdentifierFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildIdentifier() {
        return UUID.randomUUID().toString();
    }
}
