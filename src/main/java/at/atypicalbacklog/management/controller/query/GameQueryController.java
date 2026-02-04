package at.atypicalbacklog.management.controller.query;

import at.atypicalbacklog.management.dto.query.GameQueryResultDto;
import at.atypicalbacklog.management.service.query.GameQueryService;
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
