package at.atypicalbacklog.management.timetracking.service;

import at.atypicalbacklog.management.timetracking.domain.TimeBooking;
import at.atypicalbacklog.management.timetracking.dto.TimeBookingResponse;
import at.atypicalbacklog.management.timetracking.repository.TimeBookingRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeBookingQueryService {

    private final TimeBookingRepository repository;

    public TimeBookingQueryService(TimeBookingRepository repository) {
        this.repository = repository;
    }

    public List<TimeBookingResponse> findByPlaythroughUid(String playthroughUid) {
        List<TimeBooking> bookings = repository.findByPlaythroughUidOrderByCreationDateAsc(playthroughUid);
        return bookings.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private TimeBookingResponse toResponse(TimeBooking booking) {
        return new TimeBookingResponse(
            booking.getUid(),
            booking.getCreationDate(),
            booking.getPlaythroughUid(),
            booking.getStartTime(),
            booking.getEndTime()
        );
    }
}
