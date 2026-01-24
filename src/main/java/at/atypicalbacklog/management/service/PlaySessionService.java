package at.atypicalbacklog.management.service;

import at.atypicalbacklog.management.domain.TimeBooking;
import at.atypicalbacklog.management.repository.TimeBookingRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class PlaySessionService {

    private final TimeBookingRepository repository;

    public PlaySessionService(TimeBookingRepository repository) {
        this.repository = repository;
    }

    public TimeBooking startSession(String playthroughUid) {
        repository.findFirstByPlaythroughUidAndEndTimeIsNull(playthroughUid)
            .ifPresent(tb -> {
                throw new IllegalStateException("Session already running");
            });

        TimeBooking booking = new TimeBooking(
            UUID.randomUUID().toString(),
            playthroughUid,
            Instant.now()
        );

        return repository.save(booking);
    }

    public Duration stopSession(String playthroughUid) {
        TimeBooking booking = repository
            .findFirstByPlaythroughUidAndEndTimeIsNull(playthroughUid)
            .orElseThrow(() -> new IllegalStateException("No active session"));

        Instant end = Instant.now();
        booking.stop(end);
        repository.save(booking);

        return Duration.between(booking.getStartTime(), end);
    }
}

