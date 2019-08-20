package com.github.kirksc1.sagacious.annotation;

/**
 * IndentifierFactory defines the responsibilities for classes that will build unique identifiers for sagas and their
 * participants.
 */
public interface IdentifierFactory {

    /**
     * Build a new unique identifier.
     * @return A unique identifier.
     */
    String buildIdentifier();
}
