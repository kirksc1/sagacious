package com.github.kirksc1.sagacious.integrationtest.shippingservice;

import com.github.kirksc1.sagacious.integrationapp.shippingservice.Shipment;
import com.github.kirksc1.sagacious.integrationapp.shippingservice.ShipmentRepository;
import com.github.kirksc1.sagacious.integrationapp.shippingservice.ShipmentResource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShipmentServiceIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    ShipmentRepository shipmentRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    @After
    public void after() {
        shipmentRepository.deleteAll();
    }

    @Test
    public void testCreatePayment() {
        ShipmentResource shipmentResource = new ShipmentResource();
        shipmentResource.setDestinationId("destination");
        shipmentResource.setItems(new ArrayList<>());

        given()
                .body(shipmentResource)
                .contentType("application/json")
                .when()
                .post(URI.create("http://localhost:" + port + "/shipping-service/shipments"))
                .then()
                .statusCode(201);

        Assert.assertEquals(1, shipmentRepository.count());
    }

    @Test
    public void testCancelPayment() {
        ShipmentResource shipmentResource = new ShipmentResource();
        shipmentResource.setDestinationId("destination");
        shipmentResource.setItems(new ArrayList<>());

        String guid = given()
                .body(shipmentResource)
                .contentType("application/json")
                .when()
                .post(URI.create("http://localhost:" + port + "/shipping-service/shipments"))
                .then()
                .statusCode(201)
                .extract().body().asString();

        given()
                .when()
                .delete(URI.create("http://localhost:" + port + "/shipping-service/shipments/" + guid))
                .then()
                .statusCode(204);

        Assert.assertEquals(1, shipmentRepository.count());

        Shipment shipment = shipmentRepository.findById(guid).orElse(null);
        Assert.assertEquals("Cancelled", shipment.getStatus());
    }
}
