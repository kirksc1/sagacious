package com.github.kirksc1.sagacious.integrationapp.orderservice.payments;

import lombok.Data;

@Data
public class Payment {
    private String paymentDeviceId;
    private Float amount;
}
