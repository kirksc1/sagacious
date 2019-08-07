# Customizing @SagaParticipant

## Custom Identifiers
An alternative concrete implementation of the IdentifierFactory interface can be used
to alter the structure of the unique identifiers used for Participants.

The default implementation can be overridden by creating a bean named "participantIdentifierFactory".
```java
@Bean
IdentifierFactory participantIdentifierFactory() {
    return new MyCustomIdentifierFactory();
}
```
If the customization should only be applied to a subset of @SagaParticipant usages, then 
the new IdentifierFactory implementation should use a custom bean name.
```java
@Bean
IdentifierFactory customIdentifierFactory() {
    return new MyCustomIdentifierFactory();
}
```
Once the SagaManager is available as a bean, it can be utilized by providing the bean 
name to the @SagaOrchestrated annotation.
```java
@SagaParticipant(participantIdentifierFactory="customIdentifierFactory")
public void doParticipantWork() {
    //do work
}
```

## Annotation Ordering
Sagacious uses an Aspect that operates around @SagaParticipant annotated methods.  At
times it may be necessary to alter the order in which annotations are applied.  The spring
Order (defaults to 0) for the aspect can be set by overriding the aspect bean.
```java
@Bean
public SagaParticipantAspect sagaParticipantAspect(ApplicationContext context) {
    int customOrder = 1;
    return new SagaParticipantAspect(context, customOrder);
}
``` 