# Sagacious
[![Build Status](https://travis-ci.org/kirksc1/sagacious.svg?branch=master)](https://travis-ci.org/kirksc1/sagacious)
[![codecov](https://codecov.io/gh/kirksc1/sagacious/branch/master/graph/badge.svg)](https://codecov.io/gh/kirksc1/sagacious)
## Purpose
Sagacious is an effort to provide a framework for implementing the saga design pattern in a 
non-invasive manner.  Its primary focus is on simplifying the orchestration style of sagas.  
Although usable in any spring-enabled service, its designed for use within Spring Boot
applications.

## Setup
Sagacious uses 2 database entities and its default implementation uses JPA.  The entities
(Saga and Participant) have the following table structures within an RDBMS.

**Saga**
```sql
CREATE TABLE saga (
    identifier VARCHAR(255) NOT NULL, 
    completed BOOLEAN NOT NULL, 
    failed BOOLEAN NOT NULL, 
    PRIMARY KEY (identifier)
)
```

**Participant**
```sql
CREATE TABLE participant (
    identifier VARCHAR(255) NOT NULL, 
    action_definition VARCHAR(4000) NOT NULL, -- or CLOB as needed
    fail_completed BOOLEAN, 
    order_index INTEGER NOT NULL, 
    saga_id VARCHAR(255) NOT NULL, 
    PRIMARY KEY (identifier)
)
```

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
The method's parameter or return value may be provided to a factory bean 
(named *paymentActionDefinitionFactory* below) method that creates a definition for the compensating action.

Annotating a parameter with @ParticipantData indicates it should be passed to the factory as input.  
```java
@SagaParticipant(actionDefinitionFactory="paymentActionDefinitionFactory")
public String initiatePayment(@ParticipantData String paymentDeviceId, Float amount) throws FailedPaymentException {
    //more clever business logic
}
```
If no parameter is annotated with @ParticipantData, then the method's return value will be 
passed to the factory as input.
```java
@SagaParticipant(actionDefinitionFactory="paymentActionDefinitionFactory")
public String initiatePayment(String paymentDeviceId, Float amount) throws FailedPaymentException {
    //more clever business logic
}
```
Reference: [PaymentServiceClient.java](sagacious-sample/src/main/java/com/github/kirksc1/sagacious/sample/orderservice/payments/PaymentServiceClient.java)

**NOTE: @SagaParticipant may also be [customized](docs/sagaparticipant-customizations.md).**
### Saga State
Sagas have two key statuses that influence its behavior.
- Completed: Indicates that all known processing is complete on the Saga.  This processing 
could apply to either successful processing or processing of compensating actions upon a Saga
failure.
- Failed: Indicates that a Saga has failed.

The default SagaManager implementation is the [SimpleSagaManager](docs/simplesagamanager.md)

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

Sagacious supports the following executors:
 - [RestTemplateExecutor](docs/resttemplateexecutor.md) (sagacious-api) - communicates actions over HTTP
 - [JmsTemplateExecutor](docs/jmstemplateexecutor.md) (sagacious-jms) - communicates actions over JMS
 
# Sagacious Server Configurations
Sagacious provides server configurations through the use of the following server
annotations:
- [@EnableParticipantServer](docs/enableparticipantserver.md) : Enable limited interactions
to support remote participants.  The primary use case for this functionality is
to support applications orchestrating sagas whose participants are orchestrating
their own sagas, enabling a single saga to cover all participants.