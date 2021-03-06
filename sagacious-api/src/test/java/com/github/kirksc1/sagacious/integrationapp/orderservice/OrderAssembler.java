package com.github.kirksc1.sagacious.integrationapp.orderservice;

import java.util.stream.Collectors;


public class OrderAssembler {

    public Order assembleOrder(OrderResource resource) {
        Order retVal = new Order();

        if (resource != null) {
            retVal.setPaymentDeviceId(resource.getPaymentDeviceId());
            retVal.setShippingDestinationId(resource.getShippingDestinationId());
            retVal.setTotalAmount(resource.getTotalAmount());
            retVal.setItems(
                    resource.getItems().stream()
                            .map(this::assembleOrderItem)
                            .collect(Collectors.toList())
            );
        }

        return retVal;
    }

    private OrderItem assembleOrderItem(OrderItemResource resource) {
        OrderItem retVal = new OrderItem();

        retVal.setItemId(resource.getItemId());
        retVal.setCount(resource.getCount());

        return retVal;
    }
}
