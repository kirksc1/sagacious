package com.github.kirksc1.sagacious.sample.orderservice;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "user_order")
public class Order {

    @Id
    private String guid;
    private String paymentDeviceId;
    private Float totalAmount;
    private String shippingDestinationId;
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "orderId")
    private List<OrderItem> items;

    private String status;
}
