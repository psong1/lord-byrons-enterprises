package com.lordbyronsenterprises.server.payment;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
