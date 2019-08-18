package com.github.kirksc1.sagacious.action.web;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

public class RestTemplateExecutorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructorRestTemplate_whenRestTemplateNull_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("RestTemplate");

        new RestTemplateExecutor(null);
    }

    @Test
    public void testConstructorRestTemplateOrder_whenRestTemplateNull_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("RestTemplate");

        new RestTemplateExecutor(null, 1);
    }

    @Test
    public void testGetOrder_whenDefaultOrder_thenOrderIsZero() {
        assertEquals(0, new RestTemplateExecutor(new RestTemplate()).getOrder());
    }

    @Test
    public void testGetOrder_whenProvidedOrder_thenOrderIsTheProvided() {
        assertEquals(1, new RestTemplateExecutor(new RestTemplate(), 1).getOrder());
    }
}
