package at.atypicalbacklog.management.timetracking.controller;

import at.atypicalbacklog.management.timetracking.dto.TimeBookingResponse;
import at.atypicalbacklog.management.timetracking.service.TimeBookingQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query/time-bookings")
public class TimeBookingQueryController {

    private final TimeBookingQueryService service;

    public TimeBookingQueryController(TimeBookingQueryService service) {
        this.service = service;
    }

    @GetMapping
    public List<TimeBookingResponse> findByPlaythroughUid(
        @RequestParam String playthroughUid
    ) {
        return service.findByPlaythroughUid(playthroughUid);
    }
}
