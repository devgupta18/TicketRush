package com.ticketrush.booking_service.repository;

import com.ticketrush.booking_service.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
