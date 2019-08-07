# Customizing @SagaOrchestrated

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