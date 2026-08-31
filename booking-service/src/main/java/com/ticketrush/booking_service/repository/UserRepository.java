package com.ticketrush.booking_service.repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
