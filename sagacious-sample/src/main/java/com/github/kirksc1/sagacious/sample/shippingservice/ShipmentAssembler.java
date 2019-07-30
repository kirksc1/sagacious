package com.github.kirksc1.sagacious.sample.shippingservice;

import com.github.kirksc1.sagacious.sample.orderservice.OrderItem;
import com.github.kirksc1.sagacious.sample.orderservice.OrderItemResource;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ShipmentAssembler {

    public Shipment assembleShipment(ShipmentResource resource) {
        Shipment retVal = new Shipment();
        retVal.setDestinationId(resource.getDestinationId());
        retVal.setItems(
                resource.getItems().stream()
                        .map(this::assembleShipmentItem)
                        .collect(Collectors.toList())
        );

        return retVal;
    }

    private ShipmentItem assembleShipmentItem(ShipmentItemResource resource) {
        ShipmentItem retVal = new ShipmentItem();

        retVal.setItemId(resource.getItemId());
        retVal.setCount(resource.getCount());

        return retVal;
    }
}
