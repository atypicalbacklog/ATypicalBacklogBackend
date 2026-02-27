package at.atypicalbacklog.management.hltb.controller;

import at.atypicalbacklog.management.hltb.dto.HltbEstimateResponse;
import at.atypicalbacklog.management.hltb.dto.UpsertHltbEstimateRequest;
import at.atypicalbacklog.management.hltb.service.HltbEstimateCommandService;
import at.atypicalbacklog.management.hltb.service.HltbEstimateQueryService;

import org.springframework.web.bind.annotation.*;

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
        return queryService.getByGameInstanceUid(gameInstanceUid).orElse(null);
    }
}
