package com.github.kirksc1.sagacious.sample.shippingservice;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "shipment")
@Data
public class Shipment {

    @Id
    private String guid;
    private String destinationId;
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "shipmentId")
    private List<ShipmentItem> items;
    private String status;
}
