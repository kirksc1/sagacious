package com.github.kirksc1.sagacious.sample.paymentservice;

import org.springframework.stereotype.Component;

@Component
public class PaymentAssembler {

    public Payment assemblePayment(PaymentResource resource) {
        Payment retVal = new Payment();

        retVal.setPaymentDeviceId(resource.getPaymentDeviceId());
        retVal.setAmount(resource.getAmount());

        return retVal;
    }
}
