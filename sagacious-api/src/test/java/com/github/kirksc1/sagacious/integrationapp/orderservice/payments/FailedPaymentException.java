package com.github.kirksc1.sagacious.integrationapp.orderservice.payments;

public class FailedPaymentException extends Exception {

    public FailedPaymentException(Throwable cause) {
        super(cause);
    }
}
