package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.annotation.Executable;
import lombok.Getter;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompensatingActionManagerTest {

    private TestCompensatingActionDefinitionMatcher matcher = new TestCompensatingActionDefinitionMatcher(true);
    private TestCompensatingActionDefinitionMatcher noMatcher = new TestCompensatingActionDefinitionMatcher(false);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @After
    public void after() {
        matcher.reset();
    }

    @Test
    public void testConstructor_whenNullMatcher_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("CompensatingActionDefinitionMatcher");

        new CompensatingActionManager(null, new ArrayList<>());
    }

    @Test
    public void testConstructor_whenNullList_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("executor list");

        new CompensatingActionManager(new SimpleCompensatingActionDefinitionMatcher(), null);
    }

    @Test
    public void testExecute_whenExecutorsListEmpty_thenReturnFalse() {
        List<CompensatingActionExecutor> executors = new ArrayList<>();
        CompensatingActionManager manager = new CompensatingActionManager(matcher, executors);

        assertEquals(false, manager.execute(new CompensatingActionDefinition()));
        assertEquals(0, matcher.getSchemes().size());
    }

    @Test
    public void testExecute_whenOneExecutableFalse_thenOneCheckedAndReturnFalse() {
        List<CompensatingActionExecutor> executors = new ArrayList<>();
        executors.add(new FirstCompensatingActionExecutor(false));
        CompensatingActionManager manager = new CompensatingActionManager(matcher, executors);

        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("first://test.com");

        assertEquals(false, manager.execute(definition));
        assertEquals(1, matcher.getSchemes().size());
        assertEquals("first", matcher.getSchemes().get(0));
    }

    @Test
    public void testExecute_whenOneExecutableTrue_thenOneCheckedAndReturnTrue() {
        List<CompensatingActionExecutor> executors = new ArrayList<>();
        executors.add(new FirstCompensatingActionExecutor(true));
        CompensatingActionManager manager = new CompensatingActionManager(matcher, executors);

        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("first://test.com");

        assertEquals(true, manager.execute(definition));
        assertEquals(1, matcher.getSchemes().size());
        assertEquals("first", matcher.getSchemes().get(0));
    }

    @Test
    public void testExecute_whenTwoExecutables_thenBothChecked() {
        List<CompensatingActionExecutor> executors = new ArrayList<>();
        executors.add(new SecondCompensatingActionExecutor());
        CompensatingActionManager manager = new CompensatingActionManager(noMatcher, executors);

        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("test://test.com");

        assertEquals(false, manager.execute(definition));
        assertEquals(2, noMatcher.getSchemes().size());
        assertEquals("second1", noMatcher.getSchemes().get(0));
        assertEquals("second2", noMatcher.getSchemes().get(1));
    }

    @Test
    public void testExecute_whenTwoExecutors_thenAllChecked() {
        List<CompensatingActionExecutor> executors = new ArrayList<>();
        executors.add(new SecondCompensatingActionExecutor());
        executors.add(new FirstCompensatingActionExecutor(false));
        CompensatingActionManager manager = new CompensatingActionManager(noMatcher, executors);

        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("test://test.com");

        assertEquals(false, manager.execute(definition));
        assertEquals(3, noMatcher.getSchemes().size());
        assertEquals("second1", noMatcher.getSchemes().get(0));
        assertEquals("second2", noMatcher.getSchemes().get(1));
        assertEquals("first", noMatcher.getSchemes().get(2));
    }

    @Executable(scheme = "first")
    class FirstCompensatingActionExecutor implements CompensatingActionExecutor {
        private boolean success;
        public FirstCompensatingActionExecutor(boolean success) {
            this.success = success;
        }
        @Override
        public boolean execute(CompensatingActionDefinition definition) {
            return success;
        }
    }

    @Executable(scheme = "second1")
    @Executable(scheme = "second2")
    class SecondCompensatingActionExecutor implements CompensatingActionExecutor {
        @Override
        public boolean execute(CompensatingActionDefinition definition) {
            return false;
        }
    }

    @Getter
    static class TestCompensatingActionDefinitionMatcher implements CompensatingActionDefinitionMatcher {
        private List<String> schemes = new ArrayList<>();
        private boolean matches;

        public TestCompensatingActionDefinitionMatcher(boolean matches) {
            this.matches = matches;
        }

        @Override
        public boolean matches(Executable specification, CompensatingActionDefinition instance) {
            schemes.add(specification.scheme());
            return matches;
        }

        public void reset() {
            schemes.clear();
        }
    }
}
