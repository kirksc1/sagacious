package com.github.kirksc1.sagacious.sample.shippingservice;

import com.github.kirksc1.sagacious.sample.orderservice.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {
}
