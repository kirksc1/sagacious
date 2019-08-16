package com.github.kirksc1.sagacious.sample.orderservice;

import com.github.kirksc1.sagacious.repository.SagaRepository;
import com.github.kirksc1.sagacious.sample.paymentservice.PaymentRepository;
import com.github.kirksc1.sagacious.sample.shippingservice.ShipmentRepository;
import org.junit.After;
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
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderServiceIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ShipmentRepository shipmentRepository;

    @Autowired
    SagaRepository sagaRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    @After
    public void after() {
        orderRepository.deleteAll();
        paymentRepository.deleteAll();
        shipmentRepository.deleteAll();
        sagaRepository.deleteAll();
    }

    @Test
    public void testCreateOrder() {
        OrderResource orderResource = new OrderResource();
        orderResource.setShippingDestinationId("destination");
        orderResource.setTotalAmount(1.0f);
        orderResource.setItems(new ArrayList<>());

        given()
            .body(orderResource)
            .contentType("application/json")
        .when()
            .post(URI.create("http://localhost:" + port + "/order-service/orders"))
        .then()
            .statusCode(201);

        assertEquals(1, orderRepository.count());
        assertEquals(1, paymentRepository.count());
        assertEquals(1, shipmentRepository.count());
        assertEquals(1, sagaRepository.count());

        assertEquals("Completed", orderRepository.findAll().get(0).getStatus());

        assertEquals("Completed", paymentRepository.findAll().get(0).getStatus());

        assertEquals("Completed", shipmentRepository.findAll().get(0).getStatus());

        assertEquals(false, sagaRepository.findAll().get(0).isFailed());
        assertEquals(true, sagaRepository.findAll().get(0).isCompleted());
    }


    @Test
    public void testCreateOrderThatFailsAfterShipping() {
        OrderResource orderResource = new OrderResource();
        orderResource.setTotalAmount(1.0f);
        orderResource.setShippingDestinationId("fail-after-shipping");
        orderResource.setItems(new ArrayList<>());

        given()
                .body(orderResource)
                .contentType("application/json")
                .when()
                .post(URI.create("http://localhost:" + port + "/order-service/orders"))
                .then()
                .statusCode(500);

        assertEquals(1, orderRepository.count());
        assertEquals(1, paymentRepository.count());
        assertEquals(1, shipmentRepository.count());
        assertEquals(1, sagaRepository.count());

        assertEquals("Errored", orderRepository.findAll().get(0).getStatus());

        assertEquals("Cancelled", paymentRepository.findAll().get(0).getStatus());

        assertEquals("Cancelled", shipmentRepository.findAll().get(0).getStatus());

        assertEquals(true, sagaRepository.findAll().get(0).isFailed());
        assertEquals(true, sagaRepository.findAll().get(0).isCompleted());
    }

    @Test
    public void testCreateOrderThatFailsDuringPayment() {
        OrderResource orderResource = new OrderResource();
        orderResource.setTotalAmount(-1.0f);
        orderResource.setShippingDestinationId("destination");
        orderResource.setItems(new ArrayList<>());

        given()
                .body(orderResource)
                .contentType("application/json")
                .when()
                .post(URI.create("http://localhost:" + port + "/order-service/orders"))
                .then()
                .statusCode(500);

        assertEquals(1, orderRepository.count());
        assertEquals(0, paymentRepository.count());
        assertEquals(0, shipmentRepository.count());
        assertEquals(1, sagaRepository.count());

        assertEquals("Errored", orderRepository.findAll().get(0).getStatus());

        assertEquals(true, sagaRepository.findAll().get(0).isFailed());
        assertEquals(true, sagaRepository.findAll().get(0).isCompleted());
    }

    @Test
    public void testCreateOrderThatFailsDuringShipping() {
        OrderResource orderResource = new OrderResource();
        orderResource.setTotalAmount(1.0f);
        orderResource.setShippingDestinationId(null);
        orderResource.setItems(new ArrayList<>());

        given()
                .body(orderResource)
                .contentType("application/json")
                .when()
                .post(URI.create("http://localhost:" + port + "/order-service/orders"))
                .then()
                .statusCode(500);

        assertEquals(1, orderRepository.count());
        assertEquals(1, paymentRepository.count());
        assertEquals(0, shipmentRepository.count());
        assertEquals(1, sagaRepository.count());

        assertEquals("Errored", orderRepository.findAll().get(0).getStatus());

        assertEquals("Cancelled", paymentRepository.findAll().get(0).getStatus());

        assertEquals(true, sagaRepository.findAll().get(0).isFailed());
        assertEquals(true, sagaRepository.findAll().get(0).isCompleted());
    }

}
