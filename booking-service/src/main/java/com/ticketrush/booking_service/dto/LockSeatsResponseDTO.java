package com.ticketrush.booking_service.dto;

import java.util.List;

public record LockSeatsResponseDTO(boolean success, List<Long> unavailableSeats) {}