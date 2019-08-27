package com.github.kirksc1.sagacious.annotation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultExceptionManagerTest {

    @Test
    public void testFailOn_whenUncheckedException_thenReturnTrue() {
        DefaultExceptionManager exceptionManager = new DefaultExceptionManager();

        assertEquals(true, exceptionManager.failOn(new RuntimeException()));
    }

    @Test
    public void testFailOn_whenError_thenReturnTrue() {
        DefaultExceptionManager exceptionManager = new DefaultExceptionManager();

        assertEquals(true, exceptionManager.failOn(new RuntimeException()));
    }

    @Test
    public void testFailOn_whenCheckedException_thenReturnFalse() {
        DefaultExceptionManager exceptionManager = new DefaultExceptionManager();

        assertEquals(false, exceptionManager.failOn(new Exception()));
    }
}
