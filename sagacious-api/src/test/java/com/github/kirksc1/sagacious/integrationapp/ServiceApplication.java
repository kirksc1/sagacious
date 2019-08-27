package com.github.kirksc1.sagacious.integrationapp;

import com.github.kirksc1.sagacious.integrationapp.orderservice.Order;
import com.github.kirksc1.sagacious.integrationapp.paymentservice.Payment;
import com.github.kirksc1.sagacious.integrationapp.shippingservice.Shipment;
import com.github.kirksc1.sagacious.repository.Saga;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan( basePackageClasses = {Saga.class, Order.class, Payment.class, Shipment.class} )
@EnableJpaRepositories({"com.github.kirksc1.sagacious"})
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}
