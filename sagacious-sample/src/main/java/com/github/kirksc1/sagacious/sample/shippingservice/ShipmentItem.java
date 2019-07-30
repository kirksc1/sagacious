package com.github.kirksc1.sagacious.sample.shippingservice;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "shipment_item")
public class ShipmentItem {

    @Id
    private String guid;
    private String itemId;
    private Integer count;
}
