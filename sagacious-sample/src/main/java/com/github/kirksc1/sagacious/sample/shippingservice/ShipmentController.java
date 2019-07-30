package com.github.kirksc1.sagacious.sample.shippingservice;

import com.github.kirksc1.sagacious.sample.paymentservice.Payment;
import com.github.kirksc1.sagacious.sample.paymentservice.PaymentAssembler;
import com.github.kirksc1.sagacious.sample.paymentservice.PaymentResource;
import com.github.kirksc1.sagacious.sample.paymentservice.PaymentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shipping-service")
@RequiredArgsConstructor
public class ShipmentController {

    @NonNull
    private final ShipmentAssembler assembler;

    @NonNull
    private final ShipmentService service;

    @RequestMapping(path = "/shipments", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String createShipment(@RequestBody ShipmentResource shipmentResource) throws InvalidShipmentException {
        Shipment shipment = assembler.assembleShipment(shipmentResource);
        shipment = service.createShipment(shipment);
        return shipment.getGuid();
    }

    @RequestMapping(path = "/shipments/{guid}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelShipment(@PathVariable("guid") String guid) {
        service.cancelShipment(guid);
    }
}
