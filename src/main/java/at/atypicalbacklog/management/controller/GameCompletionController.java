package at.atypicalbacklog.management.controller;

import at.atypicalbacklog.management.dto.completion.UpdateGameCompletionRequest;
import at.atypicalbacklog.management.service.completion.GameCompletionCommandService;
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
