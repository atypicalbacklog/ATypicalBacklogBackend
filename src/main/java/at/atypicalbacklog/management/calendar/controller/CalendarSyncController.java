package at.atypicalbacklog.management.calendar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.atypicalbacklog.management.calendar.service.TimeBookingCalendarSyncService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/sync")
public class CalendarSyncController {

    private final TimeBookingCalendarSyncService syncService;

    public CalendarSyncController(TimeBookingCalendarSyncService syncService) {
        this.syncService = syncService;
    }

    /**
     * Triggers a one-off sync of unsynced time bookings to the linked Google Calendar.
     * Returns the number of bookings synced in this run.
     */
    @PostMapping("/calendar")
    public ResponseEntity<Map<String, Integer>> syncCalendar() {
        int synced = syncService.syncUnsyncedTimeBookings();
        return ResponseEntity.ok(Map.of("synced", synced));
    }
}
