package at.atypicalbacklog.management.timetracking.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.timetracking.domain.TimeBooking;
import at.atypicalbacklog.management.timetracking.dto.CreateRetroactiveSessionRequest;
import at.atypicalbacklog.management.timetracking.repository.TimeBookingRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

@Service
public class TimeTrackingService {

    private final TimeBookingRepository repository;
    private final AuditRecorder auditRecorder;

    private final ZoneId appZone;

    public TimeTrackingService(
        TimeBookingRepository repository,
        AuditRecorder auditRecorder,
        @Value("${app.timezone:Europe/Vienna}") String timezoneId
    ) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
        this.appZone = ZoneId.of(timezoneId);
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

    private static final int MAX_RETROACTIVE_MINUTES = 600;

    /**
     * Creates a closed time booking after the fact. Start time is noon on the given date;
     * end time = start + minuteCount. If that would cross midnight, start is shifted earlier
     * so both start and end are on the same day. Rejects minuteCount &gt; 600.
     */
    public TimeBooking createRetroactiveSession(
        String playthroughUid,
        CreateRetroactiveSessionRequest request
    ) {
        int minutes = request.minuteCount();
        if (minutes < 1 || minutes > MAX_RETROACTIVE_MINUTES) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "minuteCount must be between 1 and " + MAX_RETROACTIVE_MINUTES + ", got: " + minutes
            );
        }

        LocalDate date = request.date();
        LocalDateTime noon = date.atTime(12, 0);
        Instant startTime = noon.atZone(appZone).toInstant();
        Instant endTime = startTime.plus(Duration.ofMinutes(minutes));

        LocalDate endDate = endTime.atZone(appZone).toLocalDate();
        if (endDate.isAfter(date)) {
            LocalDateTime endOfDay = date.atTime(23, 59, 59, 999_000_000);
            endTime = endOfDay.atZone(appZone).toInstant();
            startTime = endTime.minus(Duration.ofMinutes(minutes));
        }

        TimeBooking booking = new TimeBooking(
            UUID.randomUUID().toString(),
            playthroughUid,
            startTime
        );
        booking.stop(endTime);

        TimeBooking saved = repository.save(booking);
        Duration duration = Duration.between(booking.getStartTime(), booking.getEndTime());
        auditRecorder.record(
            "Create Retroactive Session",
            "TimeBooking",
            "CREATE",
            Map.of(
                "playthroughUid", playthroughUid,
                "timeBookingUid", booking.getUid()
            ),
            null,
            Map.of(
                "startedAt", booking.getStartTime(),
                "stoppedAt", booking.getEndTime(),
                "durationSeconds", duration.getSeconds()
            ),
            null
        );
        return saved;
    }
}

