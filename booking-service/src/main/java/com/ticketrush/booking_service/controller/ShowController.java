package com.ticketrush.booking_service.controller;

import com.ticketrush.booking_service.dto.ShowSummaryDTO;
import com.ticketrush.booking_service.entity.Show;
import com.ticketrush.booking_service.exception.ShowNotFoundException;
import com.ticketrush.booking_service.repository.SeatRepository;
import com.ticketrush.booking_service.repository.ShowRepository;
import com.ticketrush.booking_service.service.SeatReservationService;
import com.ticketrush.booking_service.specifications.ShowSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shows")
public class ShowController {
    private final ShowRepository showRepository;
    private final SeatReservationService seatReservationService;

    public ShowController(ShowRepository showRepository, SeatReservationService seatReservationService) {
        this.showRepository = showRepository;
        this.seatReservationService = seatReservationService;
    }

    @GetMapping
    public ResponseEntity<List<ShowSummaryDTO>> browseShows(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String title) {
        Specification<Show> spec = Specification.where(ShowSpecifications.hasCity(city))
                .and(ShowSpecifications.hasDate(date))
                .and(ShowSpecifications.hasTitle(title));

        List<Show> shows = showRepository.findAll(spec);
        List<ShowSummaryDTO> showSummaryDTOS = new ArrayList<>();
        for(Show show : shows) {
            ShowSummaryDTO showSummaryDTO = showSummaryDTOMapper(show);
            showSummaryDTOS.add(showSummaryDTO);
        }

        return ResponseEntity.ok(showSummaryDTOS);
    }

    @GetMapping("/{showId}")
    public ResponseEntity<ShowSummaryDTO> getShowDetails(@PathVariable Long showId) {
        Show show = showRepository.findById(showId).orElseThrow(() -> new ShowNotFoundException("Show not found"));
        ShowSummaryDTO showSummaryDTO = showSummaryDTOMapper(show);
        return ResponseEntity.ok(showSummaryDTO);
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<Map<Long, Boolean>> getSeatAvailability(@PathVariable Long showId) {
        Map<Long, Boolean> seatAvailability = seatReservationService.getSeatAvailability(showId);
        return ResponseEntity.ok(seatAvailability);
    }

    private static ShowSummaryDTO showSummaryDTOMapper(Show show) {
        return new ShowSummaryDTO(
                show.getMovie().getTitle(),
                show.getMovie().getDescription(),
                show.getMovie().getDurationMinutes(),
                show.getMovie().getLanguage(),
                show.getMovie().getGenre(),
                show.getMovie().getReleaseDate(),
                show.getShowTime(),
                show.getVenue().getName(),
                show.getVenue().getAddress(),
                show.getVenue().getCity()
        );
    }
}
