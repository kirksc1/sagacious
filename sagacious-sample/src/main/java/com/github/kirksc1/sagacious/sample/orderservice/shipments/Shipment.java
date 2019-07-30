package com.github.kirksc1.sagacious.sample.orderservice.shipments;

import com.github.kirksc1.sagacious.sample.shippingservice.ShipmentItemResource;
import lombok.Data;

import java.util.List;

@Data
public class Shipment {

    private String destinationId;
    private List<ShipmentItem> items;
}
