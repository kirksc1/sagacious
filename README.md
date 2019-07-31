# Sagacious
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
 