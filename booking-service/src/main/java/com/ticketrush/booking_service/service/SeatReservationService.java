package com.ticketrush.booking_service.service;

import com.ticketrush.booking_service.dto.BookingResponseDTO;
import com.ticketrush.booking_service.entity.Booking;
import com.ticketrush.booking_service.exception.SeatLockOwnershipException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeatReservationService {
    private final SeatLockService seatLockService;
    private final BookingFinalizationService bookingFinalizationService;
    private static final long PAYMENT_INITIATION_THRESHOLD_SECONDS = 30;

    public SeatReservationService(SeatLockService seatLockService, BookingFinalizationService bookingFinalizationService) {
        this.seatLockService = seatLockService;
        this.bookingFinalizationService = bookingFinalizationService;
    }

    public record SeatLockResult(boolean success, List<Long> unavailableSeats) {}

    public SeatLockResult reserveSeats(Long showId, List<Long> seatNumber, Long userId) {
        List<Long> unavailableSeats = new ArrayList<>();
        List<Long> lockedSeats = new ArrayList<>();
        boolean failureEncountered = false;

        for(Long seat: seatNumber) {
            if(!failureEncountered) {
                boolean attempt = seatLockService.acquireLock(showId, seat, userId);
                if(attempt) {
                    lockedSeats.add(seat);
                } else  {
                    unavailableSeats.add(seat);
                    failureEncountered = true;
                }
            } else {
                if(seatLockService.isSeatLocked(showId, seat)) {
                    unavailableSeats.add(seat);
                }
            }
        }

        if(failureEncountered) {
            for(Long seat:lockedSeats) {
                boolean lockReleased = seatLockService.releaseLock(showId, seat, userId);
                if(!lockReleased) {
                    log.warn("Failed to release lock for seat {} on show {}", seat, showId);
                }
            }
        }

        return new SeatLockResult(!failureEncountered, unavailableSeats);
    }

    public boolean canInitiatePayment(Long showId, List<Long> seatNumber) {
        for(Long seat: seatNumber) {
            long ttlRemaining = seatLockService.getRemainingTtl(showId, seat);
            if(ttlRemaining <= PAYMENT_INITIATION_THRESHOLD_SECONDS) {
                return false;
            }
        }
        return true;
    }

    public BookingResponseDTO confirmPayment(Long showId, List<Long> seatNumber, Long userId) {
        for(Long seat: seatNumber) {
            if(!seatLockService.isLockedByUser(showId, seat, userId)) {
                throw new SeatLockOwnershipException("Seat is currently unavailable");
            }
        }

        BookingResponseDTO bookingResponseDTO = bookingFinalizationService.finalizeBooking(showId,  seatNumber, userId);

        for(Long seat: seatNumber) {
            boolean isLockReleased = seatLockService.releaseLock(showId, seat, userId);
            if(!isLockReleased) {
                log.warn("Failed to release lock for seat {} on show {}", seat, showId);
            }
        }

        // Booking Confirmation Event

        return bookingResponseDTO;
    }
}

