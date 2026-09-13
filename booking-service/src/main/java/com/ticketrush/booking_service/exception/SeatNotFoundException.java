package com.ticketrush.booking_service.exception;

import org.springframework.http.HttpStatus;

public class SeatNotFoundException extends ApplicationException {
    public SeatNotFoundException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
