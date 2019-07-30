package com.github.kirksc1.sagacious.sample.paymentservice;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "payment")
public class Payment {

    @Id
    private String guid;
    private String paymentDeviceId;
    private Float amount;
    private String status;
}
