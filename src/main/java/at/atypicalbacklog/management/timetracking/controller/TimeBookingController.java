package at.atypicalbacklog.management.timetracking.controller;

import at.atypicalbacklog.management.timetracking.domain.TimeBooking;
import at.atypicalbacklog.management.timetracking.dto.ReduceTimeBookingDurationRequest;
import at.atypicalbacklog.management.timetracking.dto.TimeBookingResponse;
import at.atypicalbacklog.management.timetracking.service.TimeTrackingService;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/time-bookings")
public class TimeBookingController {

    private final TimeTrackingService service;

    public TimeBookingController(TimeTrackingService service) {
        this.service = service;
    }

    @PutMapping("/reduce-duration")
    public TimeBookingResponse reduceDuration(@RequestBody ReduceTimeBookingDurationRequest req) {
        TimeBooking booking = service.reduceDuration(req.uid(), req.durationMinutes());
        return new TimeBookingResponse(
            booking.getUid(),
            booking.getCreationDate(),
            booking.getPlaythroughUid(),
            booking.getStartTime(),
            booking.getEndTime()
        );
    }
}
