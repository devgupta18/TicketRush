package com.ticketrush.booking_service.exception;

import org.springframework.http.HttpStatus;

public class ShowNotFoundException extends ApplicationException {
    public ShowNotFoundException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
