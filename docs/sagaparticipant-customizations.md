# Customizing @SagaParticipant

## Custom Exception Handling Behavior
Sagacious aligns itself with the Spring @Transactional failure strategy (i.e. Rollback on 
Error or unchecked exceptions, but not checked exceptions).  However, in the case of a saga
it indicates the need to fail a saga rather than rollback a transaction. The @SagaParticipant
annotation offers a similar set of attributes for customizing the saga failure behavior when
processing a saga participant.

For example...
```java
@SagaParticipant(
        failOn = FailException.class, 
        failOnClassName = {"MyFailException", "MyOtherFailException"},
        noFailOn = NoFailException.class, 
        noFailOnClassName = {"MyNoFailException", "MyOtherNoFailException"}
        )
public void doParticipant() {
    //do work
}
``` 
In this scenario, an occurrence of a FailException, MyFailException, or MyOtherFailException would all 
result in a saga failure.  However, an occurrence of a NoFailException, MyNoFailException, or 
MyOtherNoFailException would NOT result in a saga failure.  In the case of multiple matches, the 
match that most closely matches the thrown exception class in the class hierarchy will win.

### Delegating Saga Failure to @SagaOrchestrated
The management of exception handling and resulting saga failure can be delegated from the @SagaParticipant
processing to the @SagaOrchestrated.  The @SagaParticipant provides an 'autoFail' attribute that when
set to *false* will turn off all exception processing when processing a participant.

```java
@SagaParticipant(autoFail = false)
public void doParticipant() {
    //do work
}
```


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