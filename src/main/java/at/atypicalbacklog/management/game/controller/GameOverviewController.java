package at.atypicalbacklog.management.game.controller;

import at.atypicalbacklog.management.game.dto.GameOverviewQueryRequest;
import at.atypicalbacklog.management.game.dto.GameOverviewQueryResponseDto;
import at.atypicalbacklog.management.game.service.GameOverviewQueryService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/query/game-overview")
public class GameOverviewController {

    private final GameOverviewQueryService service;

    public GameOverviewController(GameOverviewQueryService service) {
        this.service = service;
    }

    @PostMapping
    public GameOverviewQueryResponseDto query(@RequestBody GameOverviewQueryRequest request) {
        return service.query(request);
    }
}
