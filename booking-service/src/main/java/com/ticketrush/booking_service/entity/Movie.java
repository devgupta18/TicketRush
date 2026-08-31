package com.ticketrush.booking_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movies")
@Data
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private int durationMinutes;
    @Column(nullable = false)
    private String language;
    private LocalDate releaseDate;
    @Enumerated(EnumType.STRING)
    private MovieGenre genre;
    @Column(nullable = false)
    private Long price;
}
