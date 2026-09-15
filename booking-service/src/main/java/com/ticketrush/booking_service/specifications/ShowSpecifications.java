package com.ticketrush.booking_service.specifications;

import com.ticketrush.booking_service.entity.Show;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShowSpecifications {
    public static Specification<Show> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null : cb.equal(cb.lower(root.get("venue").get("city")), city.toLowerCase());
    }

    public static Specification<Show> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.equal(cb.lower(root.get("movie").get("title")), title.toLowerCase());
    }

    public static Specification<Show> hasDate(LocalDate date) {
        return (root, query, cb) -> {
            if(date == null) return null;
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            return cb.and(
                    cb.greaterThanOrEqualTo(root.get("showTime") ,startOfDay),
                    cb.lessThan(root.get("showTime"),  endOfDay)
            );
        };
    }
}
