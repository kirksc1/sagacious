package com.github.kirksc1.sagacious.integrationapp.paymentservice;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @NonNull
    private PaymentRepository repository;

    public Payment createPayment(Payment payment) throws InvalidPaymentException {
        if (payment.getAmount() <= 0f) {
            throw new InvalidPaymentException("The payment amount is <= 0");
        }

        payment.setGuid(UUID.randomUUID().toString());
        payment.setStatus("Completed");

        repository.save(payment);

        return payment;
    }

    public void cancelPayment(String paymentGuid) {
        repository.findById(paymentGuid).ifPresent(payment -> {
            payment.setStatus("Cancelled");
            repository.save(payment);
        });
    }
}
