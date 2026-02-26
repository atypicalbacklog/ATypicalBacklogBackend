package at.atypicalbacklog.management.game.controller;

import at.atypicalbacklog.management.game.dto.GameInstanceQueryResultDto;
import at.atypicalbacklog.management.game.service.GameInstanceQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query/game-instances")
public class GameInstanceQueryController {

    private final GameInstanceQueryService service;

    public GameInstanceQueryController(GameInstanceQueryService service) {
        this.service = service;
    }

    @GetMapping
    public List<GameInstanceQueryResultDto> search(@RequestParam String q) {
        return service.search(q);
    }
}
