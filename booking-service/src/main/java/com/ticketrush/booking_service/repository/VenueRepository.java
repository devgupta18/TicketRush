package com.ticketrush.booking_service.repository;

import com.ticketrush.booking_service.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
