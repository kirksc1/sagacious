# Sagacious
[![Build Status](https://travis-ci.org/kirksc1/sagacious.svg?branch=master)](https://travis-ci.org/kirksc1/sagacious)
[![codecov](https://codecov.io/gh/kirksc1/sagacious/branch/master/graph/badge.svg)](https://codecov.io/gh/kirksc1/sagacious)
## Purpose
Sagacious is an effort to provide a framework for implementing the saga design pattern in a 
non-invasive manner.  Its primary focus is on simplifying the orchestration style of sagas.  
Although usable in any spring-enabled service, its designed for use within Spring Boot
applications.

## Usage
### Saga Initiation
Establish a saga to which participants can be added on an as needed basis.  Participants registered
within the execution of the method will be added to the saga.  Successful completion of the method
results in a completed saga.  Exceptions initiate the compensating actions associated with each
participant.
```java
@RequestMapping(path = "/orders", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.CREATED)
@SagaOrchestrated
public String createOrder(@RequestBody OrderResource orderResource) throws Exception {
    //clever business logic
}
```
Reference: [OrderController.java](sagacious-sample/src/main/java/com/github/kirksc1/sagacious/sample/orderservice/OrderController.java)

**NOTE: @SagaOrchestrated may also be [customized](docs/sagaorchestrated-customizations.md).**

### Participant Registration
Register a saga participant, and its compensating action, with the current saga, if present. 
The method's return value is provided to a factory bean (named *paymentActionDefinitionFactory* below)
method that creates a definition for the compensating action.  
```java
@SagaParticipant(actionDefinitionFactory="paymentActionDefinitionFactory")
public String initiatePayment(String paymentDeviceId, Float amount) throws FailedPaymentException {
    //more clever business logic
}
```
Reference: [PaymentServiceClient.java](sagacious-sample/src/main/java/com/github/kirksc1/sagacious/sample/orderservice/payments/PaymentServiceClient.java)

**NOTE: @SagaParticipant may also be [customized](docs/sagaparticipant-customizations.md).**

### Compensating Actions
Within a saga, each step has a compensating action that, upon saga failure, should effectively 
undo the step.  For example, a step that authorized payment for an order might have a compensating
action that reversed the authorization.  The compensating action need not return the entire system 
back to the pre-saga state.  It only needs to return it to effectively the same state.  Each system
may have its own view of what "effectively" means in its context.

Sagacious uses a CompensatingActionDefinition to define the details of the action that needs to
be executed for a step should a saga failure occur.  These definitions are executed by
CompensatingActionExecutors.  These executors may take many forms that communicate over various 
channels (i.e. REST, JMS).  Each executor describes the definition(s) that it can execute through 
the use of the Executable annotation.

```java
@Executable(scheme="http")
@Executable(scheme="https")
public class RestTemplateExecutor implements CompensatingActionExecutor {
    // implementation
}
```

CompensatingActionExecutor beans are checked in order (according to Spring's Ordered).  The first
bean that can execute the definition is passed the definition for execution.