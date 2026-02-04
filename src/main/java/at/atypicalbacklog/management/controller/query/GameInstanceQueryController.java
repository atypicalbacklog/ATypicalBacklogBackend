package at.atypicalbacklog.management.controller.query;

import at.atypicalbacklog.management.dto.query.GameInstanceQueryResultDto;
import at.atypicalbacklog.management.service.query.GameInstanceQueryService;
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
