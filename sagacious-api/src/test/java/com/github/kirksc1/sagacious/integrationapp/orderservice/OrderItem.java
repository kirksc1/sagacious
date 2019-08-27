package com.github.kirksc1.sagacious.integrationapp.orderservice;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "order_item")
public class OrderItem {

    @Id
    private String guid;
    private String itemId;
    private Integer count;
}
