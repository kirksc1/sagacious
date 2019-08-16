package com.github.kirksc1.sagacious.sample.orderservice.shipments;

import com.github.kirksc1.sagacious.annotation.SagaParticipant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentServiceClient {

    @NonNull
    private RestTemplate restTemplate;

    @NonNull
    private Environment environment;

    @SagaParticipant(actionDefinitionFactory="shipmentActionDefinitionFactory")
    public String initiateShipment(String destinationId, List<ShipmentItem> items) throws FailedShipmentException {
        Shipment shipment = new Shipment();
        shipment.setDestinationId(destinationId);
        shipment.setItems(items);

        URI uri = URI.create("http://localhost:" + environment.getProperty("local.server.port") + "/shipping-service/shipments");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Shipment> shipmentEntity = new HttpEntity<>(shipment, headers);
        try {
            return restTemplate.postForEntity(uri, shipmentEntity, String.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new FailedShipmentException(e);
        }
    }
}
