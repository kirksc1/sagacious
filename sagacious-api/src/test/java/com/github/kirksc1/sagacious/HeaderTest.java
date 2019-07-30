package com.github.kirksc1.sagacious;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class HeaderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor_whenValuesProvide_thenValuesGettable() {
        Header header = new Header("name", "value");

        assertEquals("name", header.getName());
        assertEquals("value", header.getValue());
    }

    @Test
    public void testConstructor_whenNullName_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name");

        new Header(null, "value");
    }

    @Test
    public void testConstructor_whenNullValues_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("value");

        new Header("name", null);
    }

}
