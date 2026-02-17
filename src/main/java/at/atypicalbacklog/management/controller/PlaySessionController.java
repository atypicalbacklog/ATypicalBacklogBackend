package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.domain.TimeBooking;
import at.atypicalbacklog.management.dto.CreateRetroactiveSessionRequest;
import at.atypicalbacklog.management.dto.StartSessionResponse;
import at.atypicalbacklog.management.dto.StopSessionResponse;
import at.atypicalbacklog.management.dto.TimeBookingResponse;
import at.atypicalbacklog.management.service.TimeTrackingService;
import at.atypicalbacklog.management.service.query.TimeBookingQueryService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/playthroughs/{playthroughUid}/sessions")
public class PlaySessionController {

    private final TimeTrackingService service;
    private final TimeBookingQueryService queryService;

    public PlaySessionController(TimeTrackingService service, TimeBookingQueryService queryService) {
        this.service = service;
        this.queryService = queryService;
    }

    @GetMapping
    public List<TimeBookingResponse> getAll(@PathVariable String playthroughUid) {
        return queryService.findByPlaythroughUid(playthroughUid);
    }

    @PostMapping("/start")
    public StartSessionResponse start(@PathVariable String playthroughUid) {
        TimeBooking tb = service.startSession(playthroughUid);
        return new StartSessionResponse(tb.getUid(), tb.getStartTime());
    }

    @PostMapping("/stop")
    public StopSessionResponse stop(@PathVariable String playthroughUid) {
        Duration d = service.stopSession(playthroughUid);
        return new StopSessionResponse(
            playthroughUid,
            null,
            null,
            d.getSeconds()
        );
    }

    /**
     * Create a time booking after the fact (user forgot to track live).
     * Start time is noon on the given date; end = start + minuteCount (max 600).
     * If that would cross midnight, start is shifted earlier so both are on the same day.
     */
    @PostMapping("/retroactive")
    public StopSessionResponse createRetroactive(
        @PathVariable String playthroughUid,
        @RequestBody CreateRetroactiveSessionRequest request
    ) {
        TimeBooking tb = service.createRetroactiveSession(playthroughUid, request);
        long durationSeconds = java.time.Duration.between(tb.getStartTime(), tb.getEndTime()).getSeconds();
        return new StopSessionResponse(
            tb.getUid(),
            tb.getStartTime(),
            tb.getEndTime(),
            durationSeconds
        );
    }
}

