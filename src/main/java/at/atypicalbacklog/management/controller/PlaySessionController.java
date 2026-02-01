package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.service.TimeTrackingService;
import at.atypicalbacklog.management.domain.TimeBooking;
import at.atypicalbacklog.management.dto.StartSessionResponse;
import at.atypicalbacklog.management.dto.StopSessionResponse;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/playthroughs/{playthroughUid}/sessions")
public class PlaySessionController {

    private final TimeTrackingService service;

    public PlaySessionController(TimeTrackingService service) {
        this.service = service;
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
}

