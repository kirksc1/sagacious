# JmsTemplateExecutor
The JmsTemplateExecutor communicates compensating actions via JMS. The default
behavior autowires in an existing JmsTemplate and uses it for JMS connections.

## Default Behavior
- Executes only on actions with a URI scheme of "jms".
- Extracts the JMS destination from the URI path element.
    - For example, a URI of "jms://activemq/test" will result in message delivery to the destination of "test".
- All headers defined will be added as JMS headers on the outbound message.
