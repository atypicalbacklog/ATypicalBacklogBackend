package at.atypicalbacklog.management.controller;

import org.springframework.web.bind.annotation.*;

import at.atypicalbacklog.management.dto.playthrough.CreatePlaythroughRequest;
import at.atypicalbacklog.management.dto.playthrough.PlaythroughResponse;
import at.atypicalbacklog.management.dto.playthrough.UpdatePlaythroughRequest;
import at.atypicalbacklog.management.service.playthrough.PlaythroughCommandService;
import at.atypicalbacklog.management.service.playthrough.query.PlaythroughQueryService;

@RestController
@RequestMapping("/api/playthroughs")
public class PlaythroughController {

    private final PlaythroughCommandService service;
    private final PlaythroughQueryService queryService;

    public PlaythroughController(PlaythroughCommandService service, PlaythroughQueryService queryService) {
        this.service = service;
        this.queryService = queryService;
    }

    @PostMapping
    public PlaythroughResponse create(@RequestBody CreatePlaythroughRequest req) {
        return service.create(req);
    }

    @PutMapping("/{uid}")
    public PlaythroughResponse update(
        @PathVariable String uid,
        @RequestBody UpdatePlaythroughRequest req
    ) {
        return service.update(uid, req);
    }

    // READ (NEW)
    @GetMapping("/{uid}")
    public PlaythroughResponse getByUid(@PathVariable String uid) {
        return queryService.getByUid(uid);
    }
}
