package com.github.kirksc1.sagacious.integrationapp.orderservice.shipments;

import lombok.Data;

import java.util.List;

@Data
public class Shipment {

    private String destinationId;
    private List<ShipmentItem> items;
}
