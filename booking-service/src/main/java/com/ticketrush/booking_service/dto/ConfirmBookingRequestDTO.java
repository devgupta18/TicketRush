package com.ticketrush.booking_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ConfirmBookingRequestDTO(
        @NotNull Long showId,
        @NotEmpty List<Long> seatNumbers
) {}