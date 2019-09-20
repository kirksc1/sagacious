package com.github.kirksc1.sagacious.action.jms;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.Header;
import com.github.kirksc1.sagacious.action.CompensatingActionExecutor;
import com.github.kirksc1.sagacious.annotation.Executable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.util.Assert;

import javax.jms.JMSException;
import javax.jms.Message;
import java.net.URI;
import java.util.Optional;

/**
 * JmsTemplateExecutor is a concrete implementation of the {@link CompensatingActionExecutor} interface
 * that can execute JMS requests as defined within a {@link CompensatingActionDefinition}.
 */
@Slf4j
@Executable(scheme="jms")
public class JmsTemplateExecutor implements CompensatingActionExecutor, Ordered {

    /**
     * The name of the attribute containing the JMS Destination value.
     */
    public static final String JMS_DESTINATION_ATTRIBUTE = "jms-destination";

    private static final int DEFAULT_ORDER = 0;

    private final JmsTemplate jmsTemplate;
    private final int order;

    /**
     * Construct a new JmsTemplateExecutor with the provided JmsTemplate.
     * @param jmsTemplate The JmsTemplate.
     */
    public JmsTemplateExecutor(JmsTemplate jmsTemplate) {
        this(jmsTemplate, DEFAULT_ORDER);
    }

    /**
     * Construct a new JmsTemplateExecutor with the provided JmsTemplate and order.
     * @param jmsTemplate The JmsTemplate.
     * @param order The spring Ordered value of the CompensatingActionExecutor.
     */
    public JmsTemplateExecutor(JmsTemplate jmsTemplate, int order) {
        Assert.notNull(jmsTemplate, "The JmsTemplate provided is null");

        this.jmsTemplate = jmsTemplate;
        this.order = order;
    }

    /**
     * {@inheritDoc}
     * <p>The JmsTemplateExecutor initiates JMS requests according to the {@link CompensatingActionDefinition}.
     * Header name/value pairs are added as JMS headers.</p>
     */
    @Override
    public boolean execute(CompensatingActionDefinition definition) {
        boolean retVal = false;

        URI uri = URI.create(definition.getUri());
        String jmsDestination = Optional.ofNullable(uri)
                .map(URI::getPath)
                .filter(path -> path.length() > 1)
                .map(path -> path.substring(1))
                .orElseThrow(() -> new IllegalArgumentException("The CompensatingActionDefinition provided did not contain a URI with a path"));

        try {
            jmsTemplate.convertAndSend(jmsDestination, definition.getBody(), buildMessagePostProcessor(definition));
            retVal = true;
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }

        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Build a MessagePostProcessor to add headers to the outbound message.
     * @param definition The CompensatingActionDefinition for the action.
     * @return The constructed MessagePostProcessor.
     */
    protected MessagePostProcessor buildMessagePostProcessor(CompensatingActionDefinition definition) {
        return new MessagePostProcessor() {
            public Message postProcessMessage(Message message) throws JMSException {
                for (Header header : definition.getHeaders()) {
                    message.setStringProperty(header.getName(), header.getValue());
                }
                return message;
            }
        };
    }
    
}
