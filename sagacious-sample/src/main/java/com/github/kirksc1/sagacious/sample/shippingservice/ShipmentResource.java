package com.github.kirksc1.sagacious.sample.shippingservice;

import lombok.Data;

import java.util.List;

@Data
public class ShipmentResource {

    private String destinationId;
    private List<ShipmentItemResource> items;
}
