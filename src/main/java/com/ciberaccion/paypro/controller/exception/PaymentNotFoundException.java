package com.ciberaccion.paypro.controller.exception;

public class PaymentNotFoundException extends RuntimeException{
    public PaymentNotFoundException(Long id) {
        super("Payment not found with id: " + id);
    }

}
