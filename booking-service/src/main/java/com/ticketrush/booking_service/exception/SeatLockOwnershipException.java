package com.ticketrush.booking_service.exception;

import org.springframework.http.HttpStatus;

public class SeatLockOwnershipException extends ApplicationException {
    public SeatLockOwnershipException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
