package com.ticketrush.booking_service.repository;

import com.ticketrush.booking_service.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findByShow_ShowIdAndSeatNumber(Long showId, Long seatNumber);
    List<Seat> findByShow_ShowId(Long showId);
}
