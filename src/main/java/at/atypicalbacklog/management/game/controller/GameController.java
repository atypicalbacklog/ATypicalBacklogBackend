package at.atypicalbacklog.management.game.controller;

import at.atypicalbacklog.management.game.dto.CreateGameRequest;
import at.atypicalbacklog.management.game.dto.GameInstanceSummaryDto;
import at.atypicalbacklog.management.game.dto.GameResponse;
import at.atypicalbacklog.management.game.service.GameInstanceQueryService;
import at.atypicalbacklog.management.game.service.GameService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService service;
    private final GameInstanceQueryService instanceQueryService;

    public GameController(GameService service, GameInstanceQueryService instanceQueryService) {
        this.service = service;
        this.instanceQueryService = instanceQueryService;
    }

    @PostMapping
    public GameResponse create(@RequestBody CreateGameRequest request) {
        return service.create(request);
    }

    @GetMapping("/{uid}")
    public GameResponse get(@PathVariable String uid) {
        return service.getById(uid);
    }

    @GetMapping("/{uid}/instances")
    public List<GameInstanceSummaryDto> getInstances(@PathVariable String uid) {
        return instanceQueryService.getInstancesByGameUid(uid);
    }

    @PutMapping("/{uid}")
    public GameResponse update(
        @PathVariable String uid,
        @RequestBody CreateGameRequest request
    ) {
        return service.update(uid, request);
    }
}
