package com.github.kirksc1.sagacious.action.jms;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.jms.core.JmsTemplate;

import static org.junit.Assert.assertEquals;

public class JmsTemplateExecutorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructorJmsTemplate_whenJmsTemplateNull_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("JmsTemplate");

        new JmsTemplateExecutor(null);
    }

    @Test
    public void testConstructorJmsTemplateOrder_whenJmsTemplateNull_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("JmsTemplate");

        new JmsTemplateExecutor(null, 1);
    }

    @Test
    public void testGetOrder_whenDefaultOrder_thenOrderIsZero() {
        assertEquals(0, new JmsTemplateExecutor(new JmsTemplate()).getOrder());
    }

    @Test
    public void testGetOrder_whenProvidedOrder_thenOrderIsTheProvided() {
        assertEquals(1, new JmsTemplateExecutor(new JmsTemplate(), 1).getOrder());
    }

    @Test
    public void testExecute_whenDefinitionHasNullUri_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("URI with a path");

        JmsTemplateExecutor executor = new JmsTemplateExecutor(new JmsTemplate());

        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("jms://activemq/");

        executor.execute(definition);
    }

    @Test
    public void testExecute_whenDefinitionHasNoPath_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("URI with a path");

        JmsTemplateExecutor executor = new JmsTemplateExecutor(new JmsTemplate());

        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("jms://activemq");

        executor.execute(definition);
    }

    @Test
    public void testExecute_whenDefinitionHasEmptyPath_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("URI with a path");

        JmsTemplateExecutor executor = new JmsTemplateExecutor(new JmsTemplate());

        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("jms://activemq/");

        executor.execute(definition);
    }
}
