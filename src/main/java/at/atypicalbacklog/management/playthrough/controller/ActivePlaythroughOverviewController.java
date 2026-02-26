package at.atypicalbacklog.management.playthrough.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.atypicalbacklog.management.playthrough.dto.ActivePlaythroughOverviewRow;
import at.atypicalbacklog.management.playthrough.service.ActivePlaythroughOverviewQueryService;

@RestController
@RequestMapping("/api/query/active-playthroughs")
public class ActivePlaythroughOverviewController {

    private final ActivePlaythroughOverviewQueryService service;

    public ActivePlaythroughOverviewController(
        ActivePlaythroughOverviewQueryService service
    ) {
        this.service = service;
    }

    @GetMapping
    public List<ActivePlaythroughOverviewRow> getActive() {
        return service.loadActive();
    }
}
