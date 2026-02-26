package at.atypicalbacklog.management.playthrough.controller;

import at.atypicalbacklog.management.playthrough.dto.ActivePlaythroughOverviewRow;
import at.atypicalbacklog.management.playthrough.dto.PlaythroughSearchRequest;
import at.atypicalbacklog.management.playthrough.service.PlaythroughHistoryQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query/playthroughs/history")
public class PlaythroughHistoryController {

    private final PlaythroughHistoryQueryService service;

    public PlaythroughHistoryController(
        PlaythroughHistoryQueryService service
    ) {
        this.service = service;
    }

    @PostMapping("/search")
    public List<ActivePlaythroughOverviewRow> search(
        @RequestBody PlaythroughSearchRequest req
    ) {
        return service.search(req);
    }
}