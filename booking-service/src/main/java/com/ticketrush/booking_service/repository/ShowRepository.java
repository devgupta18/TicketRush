package com.ticketrush.booking_service.repository;

import com.ticketrush.booking_service.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShowRepository extends JpaRepository<Show, Long>, JpaSpecificationExecutor<Show> {
}
