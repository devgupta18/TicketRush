package com.ticketrush.booking_service.service;

import com.ticketrush.booking_service.dto.BookingResponseDTO;
import com.ticketrush.booking_service.entity.*;
import com.ticketrush.booking_service.exception.SeatNotFoundException;
import com.ticketrush.booking_service.exception.ShowNotFoundException;
import com.ticketrush.booking_service.exception.UserNotFoundException;
import com.ticketrush.booking_service.repository.BookingRepository;
import com.ticketrush.booking_service.repository.SeatRepository;
import com.ticketrush.booking_service.repository.ShowRepository;
import com.ticketrush.booking_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingFinalizationService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;

    public BookingFinalizationService(BookingRepository bookingRepository, UserRepository userRepository, SeatRepository seatRepository, ShowRepository showRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
    }

    @Transactional
    public BookingResponseDTO finalizeBooking(Long showId, List<Long> seatNumbers, Long userId) {
        Booking booking = new Booking();
        booking.setUser(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found!")));
        List<Seat> seats = new ArrayList<>();
        for (Long seatNumber : seatNumbers) {
            Seat seat = seatRepository.findByShow_ShowIdAndSeatNumber(showId,seatNumber).orElseThrow(() -> new SeatNotFoundException("Seat Not Found"));
            seats.add(seat);
        }
        booking.setSeats(seats);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        Show show = showRepository.findById(showId).orElseThrow(() -> new ShowNotFoundException("Show Not Found"));
        BigDecimal price = show.getPrice();
        booking.setTotalAmount(price.multiply(BigDecimal.valueOf(seats.size())));
        booking.setCreatedAt(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);

        for(Seat seat : seats) {
            seat.setBooking(savedBooking);
            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);
        }

        return new BookingResponseDTO(
                savedBooking.getBookingId(),
                BookingStatus.CONFIRMED,
                savedBooking.getTotalAmount(),
                seatNumbers,
                show.getMovie().getTitle(),
                show.getVenue().getName(),
                show.getShowTime()
        );
    }
}
