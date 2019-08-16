package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.action.SimpleCompensatingActionDefinitionMatcher;
import com.github.kirksc1.sagacious.annotation.Executable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimpleCompensatingActionDefinitionMatcherTest {

    private SimpleCompensatingActionDefinitionMatcher matcher;

    @Before
    public void before() {
        matcher = new SimpleCompensatingActionDefinitionMatcher();
    }

    @Test
    public void testMatches_whenExecutableIsEmtpy_thenReturnTrue() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("http://test.com");
        Assert.assertEquals(true, matcher.matches(EmptyTestExecutor.class.getAnnotation(Executable.class), definition));
    }

    @Test
    public void testMatches_whenExecutableIsEmtpyAndUriNull_thenReturnTrue() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        Assert.assertEquals(true, matcher.matches(EmptyTestExecutor.class.getAnnotation(Executable.class), definition));
    }

    @Test
    public void testMatches_whenSchemeMatches_thenReturnTrue() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("http://test.com");
        Assert.assertEquals(true, matcher.matches(HttpTestExecutor.class.getAnnotation(Executable.class), definition));
    }

    @Test
    public void testMatches_whenSchemeMatchesCaseInsensitively_thenReturnTrue() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("http://test.com");
        Assert.assertEquals(true, matcher.matches(UcHttpTestExecutor.class.getAnnotation(Executable.class), definition));
    }

    @Test
    public void testMatches_whenSchemeDoesNotMatch_thenReturnFalse() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("https://test.com");
        Assert.assertEquals(false, matcher.matches(HttpTestExecutor.class.getAnnotation(Executable.class), definition));
    }

    @Test
    public void testMatches_whenSchemeButURINull_thenReturnFalse() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        Assert.assertEquals(false, matcher.matches(HttpTestExecutor.class.getAnnotation(Executable.class), definition));
    }

    @Executable
    class EmptyTestExecutor {

    }

    @Executable(scheme = "http")
    class HttpTestExecutor {

    }

    @Executable(scheme = "HTTP")
    class UcHttpTestExecutor {

    }

    @Executable(scheme = "fail")
    class FailTestExecutor {

    }
}
