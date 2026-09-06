package com.ticketrush.booking_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @Email(message = "Invalid email format") String email,
        @NotEmpty @Size(min = 8) String password)
{}
