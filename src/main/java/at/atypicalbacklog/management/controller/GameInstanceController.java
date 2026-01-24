package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.dto.CreateGameInstanceRequest;
import at.atypicalbacklog.management.dto.GameInstanceResponse;
import at.atypicalbacklog.management.service.GameInstanceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game-instances")
public class GameInstanceController {

    private final GameInstanceService service;

    public GameInstanceController(GameInstanceService service) {
        this.service = service;
    }

    @PostMapping
    public GameInstanceResponse create(@RequestBody CreateGameInstanceRequest request) {
        return service.create(request);
    }

    @GetMapping("/{uid}")
    public GameInstanceResponse get(@PathVariable String uid) {
        return service.getById(uid);
    }

    @PutMapping("/{uid}")
    public GameInstanceResponse update(
        @PathVariable String uid,
        @RequestBody CreateGameInstanceRequest request
    ) {
        return service.update(uid, request);
    }
}
