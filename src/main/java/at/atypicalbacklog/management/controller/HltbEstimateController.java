package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.dto.HltbEstimateResponse;
import at.atypicalbacklog.management.dto.UpsertHltbEstimateRequest;
import at.atypicalbacklog.management.service.HltbEstimateCommandService;
import at.atypicalbacklog.management.service.HltbEstimateQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/hltb-estimates")
public class HltbEstimateController {

    private final HltbEstimateCommandService commandService;
    private final HltbEstimateQueryService queryService;

    public HltbEstimateController(HltbEstimateCommandService commandService, HltbEstimateQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PutMapping
    public HltbEstimateResponse upsert(@RequestBody UpsertHltbEstimateRequest request) {
        return commandService.upsert(request);
    }

    @GetMapping("/game-instance/{gameInstanceUid}")
    public HltbEstimateResponse getByGameInstanceUid(@PathVariable String gameInstanceUid) {
        return queryService.getByGameInstanceUid(gameInstanceUid)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No HLTB estimate for game instance"));
    }
}
