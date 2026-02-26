package at.atypicalbacklog.management.game.controller;

import at.atypicalbacklog.management.game.dto.GameQueryResultDto;
import at.atypicalbacklog.management.game.service.GameQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query/games")
public class GameQueryController {

    private final GameQueryService service;

    public GameQueryController(GameQueryService service) {
        this.service = service;
    }

    @GetMapping
    public List<GameQueryResultDto> search(@RequestParam String q) {
        return service.search(q);
    }
}
