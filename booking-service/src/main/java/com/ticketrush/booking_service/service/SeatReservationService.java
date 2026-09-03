package com.ticketrush.booking_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeatReservationService {
    private final SeatLockService seatLockService;

    public SeatReservationService(SeatLockService seatLockService) {
        this.seatLockService = seatLockService;
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
}

