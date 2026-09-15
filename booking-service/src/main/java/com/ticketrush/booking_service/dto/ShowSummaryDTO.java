package com.ticketrush.booking_service.dto;

import com.ticketrush.booking_service.entity.MovieGenre;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ShowSummaryDTO(
        String title,
        String description,
        Integer durationMinutes,
        String language,
        MovieGenre genre,
        LocalDate releaseDate,
        LocalDateTime showTime,
        String venueName,
        String venueAddress,
        String venueCity
) {
}
