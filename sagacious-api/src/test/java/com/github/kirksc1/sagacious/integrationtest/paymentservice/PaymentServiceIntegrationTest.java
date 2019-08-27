package com.github.kirksc1.sagacious.integrationtest.paymentservice;

import com.github.kirksc1.sagacious.integrationapp.paymentservice.Payment;
import com.github.kirksc1.sagacious.integrationapp.paymentservice.PaymentRepository;
import com.github.kirksc1.sagacious.integrationapp.paymentservice.PaymentResource;
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

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentServiceIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    PaymentRepository paymentRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    @After
    public void after() {
        paymentRepository.deleteAll();
    }

    @Test
    public void testCreatePayment() {
        PaymentResource paymentResource = new PaymentResource();
        paymentResource.setPaymentDeviceId("device-1");
        paymentResource.setAmount(5.0f);

        given()
                .body(paymentResource)
                .contentType("application/json")
                .when()
                .post(URI.create("http://localhost:" + port + "/payment-service/payments"))
                .then()
                .statusCode(201);

        Assert.assertEquals(1, paymentRepository.count());
    }

    @Test
    public void testCancelPayment() {
        PaymentResource paymentResource = new PaymentResource();
        paymentResource.setPaymentDeviceId("device-1");
        paymentResource.setAmount(5.0f);

        String guid = given()
                .body(paymentResource)
                .contentType("application/json")
                .when()
                .post(URI.create("http://localhost:" + port + "/payment-service/payments"))
                .then()
                .statusCode(201)
                .extract().body().asString();

        given()
                .when()
                .delete(URI.create("http://localhost:" + port + "/payment-service/payments/" + guid))
                .then()
                .statusCode(204);

        Assert.assertEquals(1, paymentRepository.count());

        Payment payment = paymentRepository.findById(guid).orElse(null);
        Assert.assertEquals("Cancelled", payment.getStatus());
    }
}
