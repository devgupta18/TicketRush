package com.ticketrush.booking_service.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
