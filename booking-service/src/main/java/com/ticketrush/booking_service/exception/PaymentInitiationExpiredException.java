package com.ticketrush.booking_service.exception;

import org.springframework.http.HttpStatus;

public class PaymentInitiationExpiredException extends ApplicationException {
    public PaymentInitiationExpiredException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
