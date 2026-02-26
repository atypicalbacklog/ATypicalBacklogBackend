package at.atypicalbacklog.management.calendar.service;

import at.atypicalbacklog.management.game.domain.Game;
import at.atypicalbacklog.management.game.domain.GameInstance;
import at.atypicalbacklog.management.game.repository.GameInstanceRepository;
import at.atypicalbacklog.management.game.repository.GameRepository;
import at.atypicalbacklog.management.playthrough.domain.Playthrough;
import at.atypicalbacklog.management.playthrough.repository.PlaythroughRepository;
import at.atypicalbacklog.management.reference.domain.PlaythroughType;
import at.atypicalbacklog.management.reference.repository.PlaythroughTypeRepository;
import at.atypicalbacklog.management.timetracking.domain.TimeBooking;
import at.atypicalbacklog.management.timetracking.repository.TimeBookingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Syncs closed, unsynced time bookings to the linked Google Calendar.
 */
@Service
public class TimeBookingCalendarSyncService {

    private static final Logger log = LoggerFactory.getLogger(TimeBookingCalendarSyncService.class);

    private final TimeBookingRepository timeBookingRepository;
    private final PlaythroughRepository playthroughRepository;
    private final GameInstanceRepository gameInstanceRepository;
    private final GameRepository gameRepository;
    private final PlaythroughTypeRepository playthroughTypeRepository;
    private final GoogleCalendarApiService googleCalendarApiService;

    public TimeBookingCalendarSyncService(
        TimeBookingRepository timeBookingRepository,
        PlaythroughRepository playthroughRepository,
        GameInstanceRepository gameInstanceRepository,
        GameRepository gameRepository,
        PlaythroughTypeRepository playthroughTypeRepository,
        GoogleCalendarApiService googleCalendarApiService
    ) {
        this.timeBookingRepository = timeBookingRepository;
        this.playthroughRepository = playthroughRepository;
        this.gameInstanceRepository = gameInstanceRepository;
        this.gameRepository = gameRepository;
        this.playthroughTypeRepository = playthroughTypeRepository;
        this.googleCalendarApiService = googleCalendarApiService;
    }

    /**
     * Finds all time bookings with start time, end time and last_sync_date IS NULL,
     * creates a calendar event for each and sets last_sync_date on success.
     */
    @Transactional
    public int syncUnsyncedTimeBookings() {
        if (!googleCalendarApiService.isConfigured()) {
            log.debug("Google Calendar not configured; skipping sync");
            return 0;
        }
        List<TimeBooking> unsynced = timeBookingRepository
            .findByStartTimeIsNotNullAndEndTimeIsNotNullAndLastSyncDateIsNull();
        int synced = 0;
        for (TimeBooking booking : unsynced) {
            if (syncOne(booking)) {
                synced++;
            }
        }
        if (synced > 0) {
            log.info("Synced {} time booking(s) to Google Calendar", synced);
        }
        return synced;
    }

    private boolean syncOne(TimeBooking booking) {
        Playthrough playthrough = playthroughRepository.findById(booking.getPlaythroughUid()).orElse(null);
        if (playthrough == null) {
            log.warn("Playthrough not found for time booking {}; skipping", booking.getUid());
            return false;
        }
        String title = resolveGameInstanceDisplayName(playthrough.getGameInstanceUid());
        String description = buildDescription(playthrough, booking);
        boolean created = googleCalendarApiService.createEvent(
            title,
            description,
            booking.getStartTime(),
            booking.getEndTime()
        );
        if (created) {
            booking.setLastSyncDate(Instant.now());
            timeBookingRepository.save(booking);
            return true;
        }
        return false;
    }

    private String resolveGameInstanceDisplayName(String gameInstanceUid) {
        if (gameInstanceUid == null || gameInstanceUid.isBlank()) {
            return "(unknown)";
        }
        GameInstance gi = gameInstanceRepository.findById(gameInstanceUid).orElse(null);
        if (gi == null) {
            return gameInstanceUid;
        }
        if (gi.getName() != null && !gi.getName().isBlank()) {
            return gi.getName();
        }
        Game game = gameRepository.findById(gi.getGameUid()).orElse(null);
        return game != null ? game.getName() : gi.getGameUid();
    }

    private String buildDescription(Playthrough playthrough, TimeBooking booking) {
        String typeName = resolvePlaythroughTypeName(playthrough.getPlaythroughTypeUid());
        String playthroughUid = playthrough.getUid() != null ? playthrough.getUid() : "";
        String bookingUid = booking.getUid() != null ? booking.getUid() : "";
        return String.format(
            "Playthrough Type: %s%nPlaythrough UID: %s%nTimeBooking UID: %s",
            typeName,
            playthroughUid,
            bookingUid
        );
    }

    private String resolvePlaythroughTypeName(String playthroughTypeUid) {
        if (playthroughTypeUid == null || playthroughTypeUid.isBlank()) {
            return "(none)";
        }
        return playthroughTypeRepository.findById(playthroughTypeUid)
            .map(PlaythroughType::getName)
            .orElse(playthroughTypeUid);
    }
}
