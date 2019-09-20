package com.github.kirksc1.sagacious;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class CompensatingActionDefinitionTest {

    @Test
    public void testSetUri_whenUriSet_thenUriGettable() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("uri");

        assertEquals("uri", definition.getUri());
    }

    @Test
    public void testSetBody_whenBodySet_thenBodyGettable() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setBody("body");

        assertEquals("body", definition.getBody());
    }

    @Test
    public void testSetAttributes_whenAttributesSet_thenAttributesGettable() {
        List<Attribute> attributes = new ArrayList<>();
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setAttributes(attributes);

        assertSame(attributes, definition.getAttributes());
    }

    @Test
    public void testSetHeaders_whenHeadersSet_thenHeadersGettable() {
        List<Header> headers = new ArrayList<>();
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setHeaders(headers);

        assertSame(headers, definition.getHeaders());
    }
}
