package at.atypicalbacklog.management.service.calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TimeBookingCalendarSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(TimeBookingCalendarSyncScheduler.class);

    private final boolean activateGoogleSync;
    private final GoogleCalendarApiService googleCalendarApiService;
    private final TimeBookingCalendarSyncService syncService;

    public TimeBookingCalendarSyncScheduler(
        @Value("${app.google.calendar.activate-google-sync:false}") boolean activateGoogleSync,
        GoogleCalendarApiService googleCalendarApiService,
        TimeBookingCalendarSyncService syncService
    ) {
        this.activateGoogleSync = activateGoogleSync;
        this.googleCalendarApiService = googleCalendarApiService;
        this.syncService = syncService;
    }

    @Scheduled(fixedDelayString = "${app.google.calendar.sync-interval-ms:900000}")
    public void syncTimeBookingsToCalendar() {
        if (!activateGoogleSync || !googleCalendarApiService.isConfigured()) {
            return;
        }
        log.debug("Running scheduled time booking → Google Calendar sync");
        syncService.syncUnsyncedTimeBookings();
    }
}
