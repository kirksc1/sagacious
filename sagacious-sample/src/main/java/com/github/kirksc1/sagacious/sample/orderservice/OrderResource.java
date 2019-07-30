package com.github.kirksc1.sagacious.sample.orderservice;

import lombok.Data;

import java.util.List;

@Data
public class OrderResource {

    private String paymentDeviceId;
    private Float totalAmount;
    private String shippingDestinationId;
    private List<OrderItemResource> items;
}
