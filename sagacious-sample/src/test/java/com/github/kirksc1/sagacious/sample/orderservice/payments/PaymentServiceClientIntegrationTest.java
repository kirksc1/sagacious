package com.github.kirksc1.sagacious.sample.orderservice.payments;

import com.github.kirksc1.sagacious.sample.paymentservice.PaymentRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentServiceClientIntegrationTest {

    @Autowired
    PaymentServiceClient client;

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
    public void testAnnoatedMethodOutsideOfSagaExecutesNormally() throws Exception {
        client.initiatePayment("device", 5.0f);

        assertEquals(1, paymentRepository.count());

        assertEquals("Completed", paymentRepository.findAll().get(0).getStatus());
    }
}
