# Customizing @SagaOrchestrated

## Custom Exception Handling Behavior
Sagacious aligns itself with the Spring @Transactional failure strategy (i.e. Rollback on 
Error or unchecked exceptions, but not checked exceptions).  However, in the case of a saga
it indicates the need to fail a saga rather than rollback a transaction. The @SagaOrchestrated
annotation offers a similar set of attributes for customizing the saga failure behavior.

For example...
```java
@SagaOrchestrated(
        failOn = FailException.class, 
        failOnClassName = {"MyFailException", "MyOtherFailException"},
        noFailOn = NoFailException.class, 
        noFailOnClassName = {"MyNoFailException", "MyOtherNoFailException"}
        )
public void startSaga() {
    //do work
}
``` 
In this scenario, an occurrence of a FailException, MyFailException, or MyOtherFailException would all 
result in a saga failure.  However, an occurrence of a NoFailException, MyNoFailException, or 
MyOtherNoFailException would NOT result in a saga failure.  In the case of multiple matches, the 
match that most closely matches the thrown exception class in the class hierarchy will win.

## Custom SagaManager
Customization of the SagaManager functionality can be accomplished through the creation of
an alternative, concrete implementation of the SagaManager interface. It can be leveraged
in one of two ways depending on how broad the customization should be applied.

A custom implementation can be configured to override the default implementation
by creating bean named "sagaManager".   This alters the default bean used for Saga 
iniation and applies to all @SagaOrchestrated usages.
```java
@Bean
SagaManager sagaManager() {
    return new MyCustomSagaManager();
}
```
 
If the customization should only be applied to a subset of @SagaOrchestrated usages, then 
the new SagaManager implementation should use a custom bean name.
```java
@Bean
SagaManager customSagaManager() {
    return new MyCustomSagaManager();
}
```
Once the SagaManager is available as a bean, it can be utilized by providing the bean 
name to the @SagaOrchestrated annotation.
```java
@SagaOrchestrated(sagaManager="customSagaManager")
public void startSaga() {
    //do work
}
```

## Custom Identifiers
An alternative concrete implementation of the IdentifierFactory interface can be used
to alter the structure of the unique identifiers used for Sagas.

The default implementation can be overridden by creating a bean named "sagaIdentifierFactory".
```java
@Bean
IdentifierFactory sagaIdentifierFactory() {
    return new MyCustomIdentifierFactory();
}
```
If the customization should only be applied to a subset of @SagaOrchestrated usages, then 
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
@SagaOrchestrated(sagaIdentifierFactory="customIdentifierFactory")
public void startSaga() {
    //do work
}
```

## Annotation Ordering
Sagacious uses an Aspect that operates around @SagaOrchestrated annotated methods.  At
times it may be necessary to alter the order in which annotations are applied.  The spring
Order (defaults to 0) for the aspect can be set by overriding the aspect bean.
```java
@Bean
public SagaOrchestratedAspect sagaOrchestratorAspect(ApplicationContext context) {
    int customOrder = 1;
    return new SagaOrchestratedAspect(context, customOrder);
}
``` 