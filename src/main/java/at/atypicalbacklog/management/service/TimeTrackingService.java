package at.atypicalbacklog.management.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.domain.TimeBooking;
import at.atypicalbacklog.management.repository.TimeBookingRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class TimeTrackingService {

    private final TimeBookingRepository repository;
    private final AuditRecorder auditRecorder;

    public TimeTrackingService(TimeBookingRepository repository, AuditRecorder auditRecorder) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
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

        TimeBooking result = repository.save(booking);
        auditRecorder.record(
            "Start Play Session",
            "TimeBooking",
            "CREATE",
            Map.of(
                "playthroughUid", playthroughUid,
                "timeBookingUid", booking.getUid()
            ),
            null,
            Map.of("startedAt", booking.getStartTime()),
            null
        );
        
        return result;
    }

    public Duration stopSession(String playthroughUid) {
        TimeBooking booking = repository
            .findFirstByPlaythroughUidAndEndTimeIsNull(playthroughUid)
            .orElseThrow(() -> new IllegalStateException("No active session"));

        Instant end = Instant.now();
        booking.stop(end);
        repository.save(booking);
        Duration duration = Duration.between(booking.getStartTime(), end);
        auditRecorder.record(
            "Stop Play Session",
            "TimeBooking",
            "UPDATE",
            Map.of("timeBookingUid", booking.getUid()),
            null,
            null,
            Map.of(
                "startedAt", booking.getStartTime(),
                "stoppedAt", booking.getEndTime(),
                "durationSeconds", duration.getSeconds()
            )
        );

        return duration;
    }
}

