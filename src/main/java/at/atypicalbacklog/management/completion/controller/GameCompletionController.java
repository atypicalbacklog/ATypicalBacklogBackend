package at.atypicalbacklog.management.completion.controller;

import at.atypicalbacklog.management.completion.dto.UpdateGameCompletionRequest;
import at.atypicalbacklog.management.completion.service.GameCompletionCommandService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game-completions")
public class GameCompletionController {

    private final GameCompletionCommandService service;

    public GameCompletionController(GameCompletionCommandService service) {
        this.service = service;
    }

    @PostMapping
    public void update(@RequestBody UpdateGameCompletionRequest req) {
        service.update(req);
    }
}
