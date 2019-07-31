package com.github.kirksc1.sagacious.sample.orderservice.payments;

import com.github.kirksc1.sagacious.SagaParticipant;
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

@Service
@RequiredArgsConstructor
public class PaymentServiceClient {

    @NonNull
    private RestTemplate restTemplate;

    @NonNull
    private Environment environment;

    @SagaParticipant(actionDefinitionFactory="paymentActionDefinitionFactory")
    public String initiatePayment(String paymentDeviceId, Float amount) throws FailedPaymentException {
        Payment payment = new Payment();
        payment.setPaymentDeviceId(paymentDeviceId);
        payment.setAmount(amount);

        URI uri = URI.create("http://localhost:" + environment.getProperty("local.server.port") + "/payment-service/payments");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Payment> paymentEntity = new HttpEntity<>(payment, headers);
        try {
            return restTemplate.postForEntity(uri, paymentEntity, String.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new FailedPaymentException(e);
        }
    }
}
