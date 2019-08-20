package com.github.kirksc1.sagacious;

import org.springframework.util.Assert;

/**
 * The ParticipantIdentifier is a unique identifier for a saga participant.  It represents a single
 * instance of a participating activity within a single saga.
 */
public final class ParticipantIdentifier {

    private final String identifier;

    /**
     * Construct a new ParticipantIdentifier.
     * @param identifier The unique identifier for the participant.
     */
    public ParticipantIdentifier(String identifier) {
        Assert.notNull(identifier, "The identifier provided is null");

        this.identifier = identifier;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return identifier;
    }
}
