package com.github.kirksc1.sagacious.sample.paymentservice;

import lombok.Data;

@Data
public class PaymentResource {
    private String paymentDeviceId;
    private Float amount;
}
