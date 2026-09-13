package com.ticketrush.booking_service.controller;

import com.ticketrush.booking_service.dto.BookingResponseDTO;
import com.ticketrush.booking_service.dto.ConfirmBookingRequestDTO;
import com.ticketrush.booking_service.service.SeatReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final SeatReservationService seatReservationService;

    public BookingController(SeatReservationService seatReservationService) {
        this.seatReservationService = seatReservationService;
    }

    @PostMapping("/confirm")
    public ResponseEntity<BookingResponseDTO> confirmBooking(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ConfirmBookingRequestDTO request) {
        BookingResponseDTO bookingResponseDTO = seatReservationService.confirmPayment(request.showId(), request.seatNumbers(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponseDTO);
    }
}
