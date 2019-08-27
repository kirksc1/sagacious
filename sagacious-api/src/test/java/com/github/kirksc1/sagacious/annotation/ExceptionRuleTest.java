package com.github.kirksc1.sagacious.annotation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class ExceptionRuleTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructorClassBoolean_whenClassNull_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("class");

        new ExceptionRule((Class<?>) null, true);
    }

    @Test
    public void testConstructorClassBoolean_whenClassNotThrowable_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("class");

        new ExceptionRule(String.class, true);
    }

    @Test
    public void testConstructorStringBoolean_whenStringNull_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("class name");

        new ExceptionRule((String) null, true);
    }

    @Test
    public void testFailOn_whenConstructedToFail_thenReturnTrue() {
        ExceptionRule rule = new ExceptionRule("Test", true);

        assertEquals(true, rule.failOn());
    }

    @Test
    public void testFailOn_whenConstructedToNotFail_thenReturnFalse() {
        ExceptionRule rule = new ExceptionRule("Test", false);

        assertEquals(false, rule.failOn());
    }

    @Test
    public void testGetMatchDepth_whenNotMatchedToString_thenReturnNoMatch() {
        ExceptionRule rule = new ExceptionRule("fail", true);

        assertEquals(ExceptionRule.NO_MATCH_DEPTH, rule.getMatchDepth(new Exception()));
    }

    @Test
    public void testGetMatchDepth_whenExactMatchToString_thenReturnZero() {
        ExceptionRule rule = new ExceptionRule("Exception", true);

        assertEquals(0, rule.getMatchDepth(new Exception()));
    }

    @Test
    public void testGetMatchDepth_whenSuperclassMatchToString_thenReturnPositive() {
        ExceptionRule rule = new ExceptionRule("java.lang.Exception", true);

        assertEquals(1, rule.getMatchDepth(new RuntimeException()));
    }

    @Test
    public void testGetMatchDepth_whenNotMatchedToClass_thenReturnNoMatch() {
        ExceptionRule rule = new ExceptionRule(IllegalArgumentException.class, true);

        assertEquals(ExceptionRule.NO_MATCH_DEPTH, rule.getMatchDepth(new IllegalStateException()));
    }

    @Test
    public void testGetMatchDepth_whenExactMatchToClass_thenReturnZero() {
        ExceptionRule rule = new ExceptionRule(Exception.class, true);

        assertEquals(0, rule.getMatchDepth(new Exception()));
    }

    @Test
    public void testGetMatchDepth_whenSuperclassMatchToClass_thenReturnPositive() {
        ExceptionRule rule = new ExceptionRule(Exception.class, true);

        assertEquals(1, rule.getMatchDepth(new RuntimeException()));
    }
}
