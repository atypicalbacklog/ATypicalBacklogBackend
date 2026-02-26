package at.atypicalbacklog.management.playthrough.controller;

import at.atypicalbacklog.management.playthrough.dto.PlaythroughQueryResultDto;
import at.atypicalbacklog.management.playthrough.service.PlaythroughQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query/playthroughs")
public class PlaythroughQueryController {

    private final PlaythroughQueryService service;

    public PlaythroughQueryController(PlaythroughQueryService service) {
        this.service = service;
    }

    @GetMapping
    public List<PlaythroughQueryResultDto> findByGameInstance(
        @RequestParam String gameInstanceUid
    ) {
        return service.findByGameInstance(gameInstanceUid);
    }
}
