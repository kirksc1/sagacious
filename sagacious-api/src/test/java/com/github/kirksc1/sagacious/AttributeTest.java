package com.github.kirksc1.sagacious;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class AttributeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor_whenValuesProvide_thenValuesGettable() {
        Attribute attribute = new Attribute("name", "value");

        assertEquals("name", attribute.getName());
        assertEquals("value", attribute.getValue());
    }

    @Test
    public void testConstructor_whenNullName_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name");

        new Attribute(null, "value");
    }

    @Test
    public void testConstructor_whenNullValues_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("value");

        new Attribute("name", null);
    }

}
