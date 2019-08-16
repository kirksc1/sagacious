package com.github.kirksc1.sagacious.action.web;

import com.github.kirksc1.sagacious.action.web.RestTemplateExecutor;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

public class RestTemplateExecutorTest {

    @Test
    public void testGetOrder_whenDefaultOrder_thenOrderIsZero() {
        assertEquals(0, new RestTemplateExecutor(new RestTemplate()).getOrder());
    }

    @Test
    public void testGetOrder_whenProvidedOrder_thenOrderIsTheProvided() {
        assertEquals(1, new RestTemplateExecutor(new RestTemplate(), 1).getOrder());
    }
}
