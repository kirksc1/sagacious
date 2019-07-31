package com.github.kirksc1.sagacious.sample.orderservice;

import com.github.kirksc1.sagacious.sample.orderservice.payments.PaymentServiceClient;
import com.github.kirksc1.sagacious.sample.orderservice.shipments.ShipmentItem;
import com.github.kirksc1.sagacious.sample.orderservice.shipments.ShipmentServiceClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    @NonNull
    private PaymentServiceClient paymentServiceClient;

    @NonNull
    private ShipmentServiceClient shipmentServiceClient;

    @NonNull
    private OrderRepository repository;

    @Transactional
    public Order createOrder(Order order) throws Exception {

        order.setGuid(UUID.randomUUID().toString());
        order.setStatus("Pending");

        order.getItems().stream()
                .forEach(orderItem -> orderItem.setGuid(UUID.randomUUID().toString()));

        repository.save(order);

        try {
            String paymentId = paymentServiceClient.initiatePayment(order.getPaymentDeviceId(), order.getTotalAmount());

            if (paymentId != null) {
                //successful payment
                shipmentServiceClient.initiateShipment(order.getShippingDestinationId(), convert(order.getItems()));
            }

            //JUST TO SIMULATE FAILURES IN TESTS
            if ("fail-after-shipping".equals(order.getShippingDestinationId())) {
                //Must be a checked exception to prevent rollback, which would rollback the participant addition
                //if nested transactions are not supported by the data store.
                throw new Exception("Forced late failure");
            }
        } catch (Exception e) {
            order.setStatus("Errored");
            repository.save(order);

            throw e;
        }

        order.setStatus("Completed");
        repository.save(order);

        return order;
    }

    private List<ShipmentItem> convert(List<OrderItem> items) {
        return items.stream()
                .map(orderItem -> {
                    ShipmentItem retVal = new ShipmentItem();
                    retVal.setItemId(orderItem.getItemId());
                    retVal.setCount(orderItem.getCount());
                    return retVal;
                }).collect(Collectors.toList());
    }

}
