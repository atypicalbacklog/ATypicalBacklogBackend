package at.atypicalbacklog.management.controller.query;

import at.atypicalbacklog.management.dto.query.*;
import at.atypicalbacklog.management.service.query.GameOverviewQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query/game-overview")
public class GameOverviewController {

    private final GameOverviewQueryService service;

    public GameOverviewController(GameOverviewQueryService service) {
        this.service = service;
    }

    @PostMapping
    public List<GameOverviewRowDto> query(@RequestBody GameOverviewQueryRequest request) {
        return service.query(request);
    }
}
