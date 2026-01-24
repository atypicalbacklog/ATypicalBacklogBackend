package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.dto.CreateGameRequest;
import at.atypicalbacklog.management.dto.GameResponse;
import at.atypicalbacklog.management.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @PostMapping
    public GameResponse create(@RequestBody CreateGameRequest request) {
        return service.create(request);
    }

    @GetMapping("/{uid}")
    public GameResponse get(@PathVariable String uid) {
        return service.getById(uid);
    }

    @PutMapping("/{uid}")
    public GameResponse update(
        @PathVariable String uid,
        @RequestBody CreateGameRequest request
    ) {
        return service.update(uid, request);
    }
}
