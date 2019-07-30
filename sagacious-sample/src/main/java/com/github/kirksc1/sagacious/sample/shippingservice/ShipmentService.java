package com.github.kirksc1.sagacious.sample.shippingservice;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ShipmentService {

    @NonNull
    private final ShipmentRepository repository;

    public Shipment createShipment(Shipment shipment) throws InvalidShipmentException {
        if (shipment.getDestinationId() == null) {
            throw new InvalidShipmentException("Destination Id provided is null");
        }

        shipment.setGuid(UUID.randomUUID().toString());
        shipment.setStatus("Completed");

        shipment.getItems().stream()
                .forEach(shipmentItem -> shipmentItem.setGuid(UUID.randomUUID().toString()));

        repository.save(shipment);

        return shipment;
    }

    public void cancelShipment(String guid) {
        repository.findById(guid).ifPresent(shipment -> {
            shipment.setStatus("Cancelled");
            repository.save(shipment);
        });
    }
}
