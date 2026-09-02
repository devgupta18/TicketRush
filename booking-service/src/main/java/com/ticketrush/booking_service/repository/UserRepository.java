package com.ticketrush.booking_service.repository;

import com.ticketrush.booking_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
