package com.github.kirksc1.sagacious.integrationapp.paymentservice;

import lombok.Data;

@Data
public class PaymentResource {
    private String paymentDeviceId;
    private Float amount;
}
