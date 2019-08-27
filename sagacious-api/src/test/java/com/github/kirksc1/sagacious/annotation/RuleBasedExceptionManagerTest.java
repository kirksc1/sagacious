package com.github.kirksc1.sagacious.annotation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RuleBasedExceptionManagerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor_whenNullCollection_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ExceptionRule collection");

        new RuleBasedExceptionManager(null);
    }

    @Test
    public void testFailOn_whenNoRules_thenRespondAsDefaultExceptionManager() {
        RuleBasedExceptionManager ruleBasedExceptionManager = new RuleBasedExceptionManager(new ArrayList<>());
        DefaultExceptionManager defaultExceptionManager = new DefaultExceptionManager();

        assertEquals(defaultExceptionManager.failOn(new Exception()), ruleBasedExceptionManager.failOn(new Exception()));
        assertEquals(defaultExceptionManager.failOn(new RuntimeException()), ruleBasedExceptionManager.failOn(new RuntimeException()));
        assertEquals(defaultExceptionManager.failOn(new Error()), ruleBasedExceptionManager.failOn(new Error()));
    }

    @Test
    public void testFailOn_whenRuleMatches_thenReturnRuleFailOn() {
        List<ExceptionRule> rules = new ArrayList<>();
        rules.add(new ExceptionRule(Exception.class, true));
        RuleBasedExceptionManager ruleBasedExceptionManager = new RuleBasedExceptionManager(rules);

        assertEquals(true, ruleBasedExceptionManager.failOn(new Exception()));
    }

    @Test
    public void testFailOn_whenMultipleRulesMatch_thenReturnLowestDepthFailOn() {
        List<ExceptionRule> rules = new ArrayList<>();
        rules.add(new ExceptionRule(Exception.class, true));
        rules.add(new ExceptionRule(RuntimeException.class, false));
        RuleBasedExceptionManager ruleBasedExceptionManager = new RuleBasedExceptionManager(rules);

        assertEquals(false, ruleBasedExceptionManager.failOn(new IllegalArgumentException()));
    }
}
