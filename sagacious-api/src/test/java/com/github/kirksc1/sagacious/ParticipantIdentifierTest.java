package com.github.kirksc1.sagacious;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class ParticipantIdentifierTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor_whenNullIdentifierProvider_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("identifier");

        new ParticipantIdentifier(null);
    }

    public void testToString_whenIdentifierProvided_thenReturnIdentifier() {
        ParticipantIdentifier identifier = new ParticipantIdentifier("test");

        assertEquals("test", identifier.toString());
    }
}
