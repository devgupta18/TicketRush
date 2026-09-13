package com.ticketrush.booking_service.dto;

import com.ticketrush.booking_service.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponseDTO(
        Long bookingId,
        BookingStatus bookingStatus,
        BigDecimal totalAmount,
        List<Long> seatNumbers,
        String movieTitle,
        String venueName,
        LocalDateTime showTime
) {}
