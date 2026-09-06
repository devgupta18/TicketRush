package com.ticketrush.booking_service.repository;

import com.ticketrush.booking_service.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
}
