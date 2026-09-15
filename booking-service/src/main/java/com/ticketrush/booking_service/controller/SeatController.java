package com.ticketrush.booking_service.controller;

import com.ticketrush.booking_service.dto.SeatNumbersRequestDTO;
import com.ticketrush.booking_service.dto.LockSeatsResponseDTO;
import com.ticketrush.booking_service.dto.ShowSummaryDTO;
import com.ticketrush.booking_service.entity.Show;
import com.ticketrush.booking_service.exception.PaymentInitiationExpiredException;
import com.ticketrush.booking_service.repository.ShowRepository;
import com.ticketrush.booking_service.service.SeatReservationService;
import com.ticketrush.booking_service.specifications.ShowSpecifications;
import jakarta.validation.Valid;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shows")
public class SeatController {
    private final SeatReservationService seatReservationService;
    private final ShowRepository  showRepository;

    public SeatController(SeatReservationService seatReservationService, ShowRepository showRepository) {
        this.seatReservationService = seatReservationService;
        this.showRepository = showRepository;
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
