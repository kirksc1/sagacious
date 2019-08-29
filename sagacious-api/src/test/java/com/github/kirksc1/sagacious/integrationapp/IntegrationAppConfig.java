package com.github.kirksc1.sagacious.integrationapp;

import com.github.kirksc1.sagacious.integrationapp.orderservice.OrderAssembler;
import com.github.kirksc1.sagacious.integrationapp.orderservice.OrderRepository;
import com.github.kirksc1.sagacious.integrationapp.orderservice.OrderService;
import com.github.kirksc1.sagacious.integrationapp.orderservice.payments.PaymentActionDefinitionFactory;
import com.github.kirksc1.sagacious.integrationapp.orderservice.payments.PaymentServiceClient;
import com.github.kirksc1.sagacious.integrationapp.orderservice.shipments.ShipmentActionDefinitionFactory;
import com.github.kirksc1.sagacious.integrationapp.orderservice.shipments.ShipmentServiceClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
public class IntegrationAppConfig {

    @Bean
    @ConditionalOnMissingBean
    PaymentServiceClient paymentServiceClient(RestTemplate restTemplate, Environment environment) {
        return new PaymentServiceClient(restTemplate, environment);
    }

    @Bean
    @ConditionalOnMissingBean
    PaymentActionDefinitionFactory paymentActionDefinitionFactory(Environment environment) {
        return new PaymentActionDefinitionFactory(environment);
    }

    @Bean
    @ConditionalOnMissingBean
    ShipmentServiceClient shipmentServiceClient(RestTemplate restTemplate, Environment environment) {
        return new ShipmentServiceClient(restTemplate, environment);
    }

    @Bean
    @ConditionalOnMissingBean
    ShipmentActionDefinitionFactory shipmentActionDefinitionFactory(Environment environment) {
        return new ShipmentActionDefinitionFactory(environment);
    }

    @Bean
    @ConditionalOnMissingBean
    OrderAssembler orderAssembler() {
        return new OrderAssembler();
    }

    @Bean
    @ConditionalOnMissingBean
    OrderService orderService(PaymentServiceClient paymentServiceClient, ShipmentServiceClient shipmentServiceClient, OrderRepository repository) {
        return new OrderService(paymentServiceClient, shipmentServiceClient, repository);
    }

}
