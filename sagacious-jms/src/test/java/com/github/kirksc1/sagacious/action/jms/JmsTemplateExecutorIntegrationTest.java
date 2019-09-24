package com.github.kirksc1.sagacious.action.jms;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.Header;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JmsTemplateExecutorIntegrationTest {

    private JmsTemplateExecutor executor;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Receiver receiver;

    @TestConfiguration
    static class TestConfig {

        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        Receiver receiver() {
            return new Receiver();
        }
    }

    @Before
    public void before() {
        executor = new JmsTemplateExecutor(jmsTemplate);
        receiver.reset();
    }

    @Test
    public void testActionSent() throws Exception {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("jms://activemq/test");
        definition.setBody("test-body");

        definition.getHeaders().add(new Header("test-header", "test-value"));

        boolean execSuccess = executor.execute(definition);
        assertTrue(execSuccess);

        receiver.getLatch().await(20000, TimeUnit.MILLISECONDS);

        Message<String> message = receiver.getMessage();
        assertEquals("test-body", message.getPayload());
        assertEquals("test-value", message.getHeaders().get("test-header"));
    }

    @Test
    public void testSendException() throws Exception {
        executor = new JmsTemplateExecutor(new JmsTemplate());

        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("jms://activemq/test");
        definition.setBody("test-body");

        boolean execSuccess = executor.execute(definition);
        assertFalse(execSuccess);
    }

    static class Receiver {

        private Message<String> message;

        private CountDownLatch latch = new CountDownLatch(1);

        public CountDownLatch getLatch() {
            return latch;
        }

        @JmsListener(destination = "test")
        public void receive(Message<String> message) throws JMSException {
            this.message = message;
            latch.countDown();
        }

        public Message<String> getMessage() {
            return message;
        }

        public void reset() {
            this.message = null;
        }
    }
}
