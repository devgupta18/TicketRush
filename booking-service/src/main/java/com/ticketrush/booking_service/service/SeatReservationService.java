package com.ticketrush.booking_service.service;

import com.ticketrush.booking_service.dto.BookingResponseDTO;
import com.ticketrush.booking_service.entity.Seat;
import com.ticketrush.booking_service.entity.SeatStatus;
import com.ticketrush.booking_service.exception.SeatLockOwnershipException;
import com.ticketrush.booking_service.exception.SeatNotFoundException;
import com.ticketrush.booking_service.repository.SeatRepository;
import com.ticketrush.booking_service.repository.ShowRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SeatReservationService {
    private final SeatLockService seatLockService;
    private final BookingFinalizationService bookingFinalizationService;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private static final long PAYMENT_INITIATION_THRESHOLD_SECONDS = 30;

    public SeatReservationService(SeatLockService seatLockService, BookingFinalizationService bookingFinalizationService, SeatRepository seatRepository, ShowRepository showRepository) {
        this.seatLockService = seatLockService;
        this.bookingFinalizationService = bookingFinalizationService;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
    }

    public record SeatLockResult(boolean success, List<Long> unavailableSeats) {}

    public SeatLockResult reserveSeats(Long showId, List<Long> seatNumber, Long userId) {
        List<Long> unavailableSeats = new ArrayList<>();
        List<Long> lockedSeats = new ArrayList<>();
        boolean failureEncountered = false;

        try {
            for(Long seat: seatNumber) {
                Seat s = seatRepository.findByShow_ShowIdAndSeatNumber(showId, seat).orElseThrow(() -> new SeatNotFoundException("Seat not found"));
                boolean isSeatAvailable = s.getStatus().equals(SeatStatus.AVAILABLE);
                if(isSeatAvailable) {
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
                } else {
                    unavailableSeats.add(seat);
                    failureEncountered = true;
                }
            }
        } catch (RuntimeException e) {
            releaseAll(showId, userId, lockedSeats);

            throw e;
        }

        if(failureEncountered) {
            releaseAll(showId, userId, lockedSeats);
        }

        return new SeatLockResult(!failureEncountered, unavailableSeats);
    }

    private void releaseAll(Long showId, Long userId, List<Long> lockedSeats) {
        for(Long seat : lockedSeats) {
            boolean lockReleased = seatLockService.releaseLock(showId, seat, userId);
            if(!lockReleased) {
                log.warn("Failed to release lock for seat {} on show {}", seat, showId);
            }
        }
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

        releaseAll(showId, userId, seatNumber);

        // Booking Confirmation Event

        return bookingResponseDTO;
    }

    public Map<Long, Boolean> getSeatAvailability(Long showId) {
        showRepository.findById(showId).orElseThrow(() -> new SeatNotFoundException("Seat not found"));

        List<Seat> totalAvailableSeats = seatRepository.findByShow_ShowId(showId);
        Map<Long, Boolean> seatAvailability = new HashMap<>();

        totalAvailableSeats.removeIf(seat -> {
            if(seat.getStatus().equals(SeatStatus.BOOKED)) {
                seatAvailability.put(seat.getSeatNumber(), false);
                return true;
            }
            return false;
        });

        List<Long> availableSeatNumbers = totalAvailableSeats.stream()
                .map(Seat::getSeatNumber)
                .toList();
        List<Boolean> seatAvailabilityList = seatLockService.areLocked(showId, availableSeatNumbers);

        for (int i = 0; i < totalAvailableSeats.size(); i++) {
            Long seatNumber = totalAvailableSeats.get(i).getSeatNumber();
            Boolean isLocked = seatAvailabilityList.get(i);

            seatAvailability.put(seatNumber, !isLocked);
        }

        return seatAvailability;
    }
}

