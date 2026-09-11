package com.ticketrush.booking_service.dto;

import java.util.List;

public record LockSeatsRequestDTO (List<Long> seatNumbers) {}
