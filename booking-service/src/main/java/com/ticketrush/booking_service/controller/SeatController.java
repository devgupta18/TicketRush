package com.ticketrush.booking_service.controller;

import com.ticketrush.booking_service.dto.LockSeatsRequestDTO;
import com.ticketrush.booking_service.dto.LockSeatsResponseDTO;
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
                                                          @Valid @RequestBody LockSeatsRequestDTO request) {
        SeatReservationService.SeatLockResult seatLockResult = seatReservationService.reserveSeats(showId, request.seatNumbers(), userId);
        if(seatLockResult.success()) {
            LockSeatsResponseDTO lockSeatsResponseDTO = new LockSeatsResponseDTO(true, seatLockResult.unavailableSeats());
            return ResponseEntity.status(HttpStatus.OK).body(lockSeatsResponseDTO);
        } else {
            LockSeatsResponseDTO lockSeatsResponseDTO = new LockSeatsResponseDTO(false, seatLockResult.unavailableSeats());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(lockSeatsResponseDTO);
        }
    }
}
