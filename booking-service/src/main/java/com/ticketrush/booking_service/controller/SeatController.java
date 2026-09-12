package com.ticketrush.booking_service.controller;

import com.ticketrush.booking_service.dto.SeatNumbersRequestDTO;
import com.ticketrush.booking_service.dto.LockSeatsResponseDTO;
import com.ticketrush.booking_service.exception.PaymentInitiationExpiredException;
import com.ticketrush.booking_service.service.SeatReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shows")
public class SeatController {
    private final SeatReservationService seatReservationService;

    public SeatController(SeatReservationService seatReservationService) {
        this.seatReservationService = seatReservationService;
    }

    @PostMapping("/{showId}/seats/lock")
    public ResponseEntity<LockSeatsResponseDTO> lockSeats(@PathVariable Long showId,
                                                          @AuthenticationPrincipal Long userId,
                                                          @Valid @RequestBody SeatNumbersRequestDTO request) {
        SeatReservationService.SeatLockResult seatLockResult = seatReservationService.reserveSeats(showId, request.seatNumbers(), userId);
        if(seatLockResult.success()) {
            LockSeatsResponseDTO lockSeatsResponseDTO = new LockSeatsResponseDTO(true, seatLockResult.unavailableSeats());
            return ResponseEntity.status(HttpStatus.OK).body(lockSeatsResponseDTO);
        } else {
            LockSeatsResponseDTO lockSeatsResponseDTO = new LockSeatsResponseDTO(false, seatLockResult.unavailableSeats());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(lockSeatsResponseDTO);
        }
    }

    @PostMapping("/{showId}/seats/initiate-payment")
    public ResponseEntity<?> initiatePayment(
            @PathVariable Long showId,
            @Valid @RequestBody SeatNumbersRequestDTO request) {
        boolean canProceed = seatReservationService.canInitiatePayment(showId, request.seatNumbers());
        if (canProceed) {
            return ResponseEntity.ok().build();
        } else {
            throw new PaymentInitiationExpiredException("Can't initiate payment due to seat lock timeout");
        }
    }
}
