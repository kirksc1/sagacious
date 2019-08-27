package com.github.kirksc1.sagacious.integrationapp.shippingservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidShipmentException extends Exception {
    public InvalidShipmentException(String message) {
        super(message);
    }
}
