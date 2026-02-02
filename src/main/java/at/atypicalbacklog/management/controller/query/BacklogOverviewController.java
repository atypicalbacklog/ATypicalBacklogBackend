package at.atypicalbacklog.management.controller.query;

import at.atypicalbacklog.management.dto.query.backlog.*;
import at.atypicalbacklog.management.service.query.BacklogOverviewQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query/backlog-overview")
public class BacklogOverviewController {

    private final BacklogOverviewQueryService service;

    public BacklogOverviewController(BacklogOverviewQueryService service) {
        this.service = service;
    }

    @PostMapping
    public List<BacklogOverviewGameDto> query(
        @RequestBody BacklogOverviewQueryRequest request
    ) {
        return service.query(request);
    }
}
