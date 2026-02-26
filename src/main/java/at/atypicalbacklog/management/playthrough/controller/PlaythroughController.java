package at.atypicalbacklog.management.playthrough.controller;

import org.springframework.web.bind.annotation.*;

import at.atypicalbacklog.management.playthrough.dto.CreatePlaythroughRequest;
import at.atypicalbacklog.management.playthrough.dto.PlaythroughResponse;
import at.atypicalbacklog.management.playthrough.dto.UpdatePlaythroughRequest;
import at.atypicalbacklog.management.playthrough.service.PlaythroughCommandService;
import at.atypicalbacklog.management.playthrough.service.PlaythroughQueryService;

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
