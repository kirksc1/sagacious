package com.github.kirksc1.sagacious.integrationapp.paymentservice;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-service")
@RequiredArgsConstructor
public class PaymentController {

    @NonNull
    private final PaymentAssembler assembler;

    @NonNull
    private final PaymentService service;

    @RequestMapping(path = "/payments", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String createPayment(@RequestBody PaymentResource paymentResource) throws InvalidPaymentException {
        Payment payment = assembler.assemblePayment(paymentResource);
        payment = service.createPayment(payment);
        return payment.getGuid();
    }

    @RequestMapping(path = "/payments/{guid}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelPayment(@PathVariable("guid") String guid) {
        service.cancelPayment(guid);
    }
}
